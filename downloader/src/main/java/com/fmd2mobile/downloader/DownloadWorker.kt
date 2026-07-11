package com.fmd2mobile.downloader

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.fmd2mobile.core.model.Chapter
import com.fmd2mobile.core.model.Download
import com.fmd2mobile.core.model.DownloadChunk
import com.fmd2mobile.core.model.DownloadStatus
import com.fmd2mobile.core.model.Manga
import com.fmd2mobile.core.repository.ChapterRepository
import com.fmd2mobile.core.repository.DownloadRepository
import com.fmd2mobile.core.repository.MangaRepository
import com.fmd2mobile.core.repository.SettingsRepository
import com.fmd2mobile.core.source.MangaSource
import com.fmd2mobile.localsource.MihonExporter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Background CoroutineWorker that processes enqueued Manga Chapter downloads.
 * Performs parallel chunked image fetching, pause/resume chunk persistence,
 * speed calculations, and background notifications.
 */
@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val downloadRepository: DownloadRepository,
    private val chapterRepository: ChapterRepository,
    private val mangaRepository: MangaRepository,
    private val settingsRepository: SettingsRepository,
    private val source: MangaSource,
    private val client: OkHttpClient,
    private val mihonExporter: MihonExporter
) : CoroutineWorker(context, workerParams) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationId = 1001
    private val channelId = "fmd2_download_channel"

    private val speedWindowBytes = AtomicLong(0)
    private var lastCheckedTime = System.currentTimeMillis()

    // Map to keep track of active chunk downloaders for cooperative cancellation
    private val activeDownloaders = ConcurrentHashMap<Int, ChunkDownloader>()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        createNotificationChannel()
        setForeground(getForegroundInfo())

        // Fetch next pending download
        val pendingDownloads = downloadRepository.getDownloadsByStatus(DownloadStatus.PENDING)
        if (pendingDownloads.isEmpty()) {
            return@withContext Result.success()
        }

        val maxConcurrency = settingsRepository.getMaxConcurrentDownloads().first()
        val activeJobs = mutableListOf<Job>()

        // Process queue limited by settings maxConcurrency
        val semaphore = SemaphoreLimit(maxConcurrency)

        coroutineScope {
            for (download in pendingDownloads) {
                if (!isActive) break

                val job = launch {
                    semaphore.withPermit {
                        processDownload(download)
                    }
                }
                activeJobs.add(job)
            }
            activeJobs.joinAll()
        }

        return@withContext Result.success()
    }

    private suspend fun processDownload(download: Download) {
        val chapter = chapterRepository.getChapterByIdOneShot(download.chapterId) ?: return
        val manga = mangaRepository.getMangaByIdOneShot(download.mangaId) ?: return

        // Update status to DOWNLOADING
        downloadRepository.updateDownload(download.copy(status = DownloadStatus.DOWNLOADING))
        chapterRepository.updateChapterStatus(chapter.id, Chapter.Status.DOWNLOADING)

        // Ticker job for speed calculation and notification updates
        val tickerJob = startSpeedCalculatorTicker(download.id, manga.title, chapter.title)

        try {
            // Fetch list of page image URLs
            val pageUrls = source.getPageList(chapter)
            if (pageUrls.isEmpty()) {
                markDownloadFailed(download, chapter)
                tickerJob.cancel()
                return
            }

            // Create temporary folder for download
            val tempDir = File(context.cacheDir, "downloads/${chapter.id}").apply { mkdirs() }
            var successfulPages = 0
            val totalPages = pageUrls.size

            // Fetch each page sequentially (pages are chunked individually)
            for ((index, pageUrl) in pageUrls.withIndex()) {
                if (isStopped) throw CancellationException("Worker stopped by system/user")

                val targetFile = File(tempDir, String.format("%03d.jpg", index + 1))
                val pageDownloadId = download.id * 1000 + index // Unique ID for each page chunks

                // Read saved chunks if any
                val savedChunks = downloadRepository.getChunksForDownload(pageDownloadId)

                val downloader = ChunkDownloader(
                    client = client,
                    url = pageUrl,
                    outputFile = targetFile,
                    downloadId = pageDownloadId,
                    initialChunks = savedChunks,
                    onProgress = { chunkIndex, bytes ->
                        // Save chunk progress in database
                        runBlocking {
                            downloadRepository.updateChunkProgress(pageDownloadId, chunkIndex, bytes)
                        }
                        speedWindowBytes.addAndGet(bytes)
                    },
                    onTotalBytesCalculated = { _ -> }
                )

                activeDownloaders[index] = downloader
                val success = downloader.download(this)
                activeDownloaders.remove(index)

                if (success) {
                    successfulPages++
                    // Delete chunk progress metadata for completed page
                    downloadRepository.deleteChunksForDownload(pageDownloadId)
                    val progressPercent = (successfulPages * 100) / totalPages
                    downloadRepository.updateDownload(
                        download.copy(
                            progress = progressPercent,
                            status = DownloadStatus.DOWNLOADING
                        )
                    )
                } else {
                    // Mark failed if unable to download page
                    markDownloadFailed(download, chapter)
                    tickerJob.cancel()
                    return
                }
            }

            // Verify and compile CBZ using MihonExporter
            // Once complete, clean up and transition status
            val finalDest = mihonExporter.exportChapter(tempDir, manga, chapter)
            if (finalDest != null) {
                downloadRepository.updateDownload(download.copy(status = DownloadStatus.COMPLETED, progress = 100))
                chapterRepository.updateChapterStatusAndInfo(
                    chapterId = chapter.id,
                    status = Chapter.Status.DOWNLOADED,
                    filePath = finalDest.absolutePath,
                    pageCount = totalPages
                )
                downloadRepository.deleteDownload(download.id)
            } else {
                markDownloadFailed(download, chapter)
            }

        } catch (e: CancellationException) {
            // Handle pausing
            downloadRepository.updateDownload(download.copy(status = DownloadStatus.PAUSED))
            chapterRepository.updateChapterStatus(chapter.id, Chapter.Status.NOT_DOWNLOADED)
        } catch (e: Exception) {
            markDownloadFailed(download, chapter)
        } finally {
            tickerJob.cancel()
        }
    }

    private suspend fun markDownloadFailed(download: Download, chapter: Chapter) {
        downloadRepository.updateDownload(download.copy(status = DownloadStatus.FAILED))
        chapterRepository.updateChapterStatus(chapter.id, Chapter.Status.FAILED)
    }

    /**
     * Ticker job that runs every 500ms to calculate download speed,
     * updates the database, and refreshes the persistent notification.
     */
    private fun CoroutineScope.startSpeedCalculatorTicker(
        downloadId: Long,
        mangaTitle: String,
        chapterTitle: String
    ): Job = launch {
        while (isActive) {
            delay(500)
            val currentTime = System.currentTimeMillis()
            val timeDiff = currentTime - lastCheckedTime
            if (timeDiff > 0) {
                val bytesInWindow = speedWindowBytes.getAndSet(0)
                val speedBytesPerSec = (bytesInWindow * 1000) / timeDiff
                
                // Update speed in database
                val currentDownload = downloadRepository.getDownloadById(downloadId)
                if (currentDownload != null) {
                    downloadRepository.updateDownload(
                        currentDownload.copy(speed = speedBytesPerSec)
                    )
                    
                    // Update Notification
                    val speedText = formatSpeed(speedBytesPerSec)
                    val progressText = "${currentDownload.progress}%"
                    val notification = createProgressNotification(
                        mangaTitle,
                        "$chapterTitle - $progressText ($speedText)"
                    )
                    notificationManager.notify(notificationId, notification)
                }
            }
            lastCheckedTime = currentTime
        }
    }



    private fun formatSpeed(bytesPerSec: Long): String {
        val kb = bytesPerSec / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1.0 -> String.format("%.1f MB/s", mb)
            kb >= 1.0 -> String.format("%.1f KB/s", kb)
            else -> "$bytesPerSec B/s"
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(notificationId, createProgressNotification("FMD2 Downloader", "Starting downloads..."))
    }

    private fun createProgressNotification(title: String, text: String): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}

/**
 * A custom Semaphore to limit concurrent background download runs.
 */
class SemaphoreLimit(private val permits: Int) {
    private val semaphore = kotlinx.coroutines.sync.Semaphore(permits)
    suspend fun <T> withPermit(action: suspend () -> T): T {
        return semaphore.withPermit(action)
    }
}
