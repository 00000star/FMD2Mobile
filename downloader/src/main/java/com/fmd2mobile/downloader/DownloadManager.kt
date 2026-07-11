package com.fmd2mobile.downloader

import android.content.Context
import androidx.work.*
import com.fmd2mobile.core.model.Chapter
import com.fmd2mobile.core.model.Download
import com.fmd2mobile.core.model.DownloadStatus
import com.fmd2mobile.core.model.Manga
import com.fmd2mobile.core.repository.ChapterRepository
import com.fmd2mobile.core.repository.DownloadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class responsible for orchestrating chapter downloads.
 * Schedules background work via WorkManager and updates statuses in the database.
 */
@Singleton
class DownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadRepository: DownloadRepository,
    private val chapterRepository: ChapterRepository
) {
    private val workManager = WorkManager.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Observable stream of all ongoing and queued downloads.
     */
    val downloads: Flow<List<Download>> = downloadRepository.getAllDownloadsFlow()

    /**
     * Enqueues a new chapter for downloading.
     * Inserts metadata into the database and triggers background worker.
     */
    fun enqueueDownload(manga: Manga, chapter: Chapter) {
        scope.launch {
            val download = Download(
                id = chapter.id,
                mangaId = manga.id,
                chapterId = chapter.id,
                status = DownloadStatus.PENDING,
                progress = 0,
                speed = 0,
                totalBytes = 0
            )
            downloadRepository.insertDownload(download)
            chapterRepository.updateChapterStatus(chapter.id, Chapter.Status.DOWNLOADING)
            
            triggerBackgroundWorker()
        }
    }

    /**
     * Pauses all ongoing and pending downloads.
     */
    fun pauseAll() {
        scope.launch {
            val downloading = downloadRepository.getDownloadsByStatus(DownloadStatus.DOWNLOADING)
            val pending = downloadRepository.getDownloadsByStatus(DownloadStatus.PENDING)
            
            (downloading + pending).forEach { download ->
                downloadRepository.updateDownload(download.copy(status = DownloadStatus.PAUSED))
                chapterRepository.updateChapterStatus(download.chapterId, Chapter.Status.NOT_DOWNLOADED)
            }
            
            // Cancel background workers
            workManager.cancelAllWorkByTag(DOWNLOAD_WORK_TAG)
        }
    }

    /**
     * Resumes all paused downloads by setting them back to PENDING.
     */
    fun resumeAll() {
        scope.launch {
            val paused = downloadRepository.getDownloadsByStatus(DownloadStatus.PAUSED)
            val failed = downloadRepository.getDownloadsByStatus(DownloadStatus.FAILED)

            (paused + failed).forEach { download ->
                downloadRepository.updateDownload(download.copy(status = DownloadStatus.PENDING, progress = 0))
                chapterRepository.updateChapterStatus(download.chapterId, Chapter.Status.DOWNLOADING)
            }

            triggerBackgroundWorker()
        }
    }

    /**
     * Cancels a specific download job by its ID, removing metadata from database.
     */
    fun cancel(id: Long) {
        scope.launch {
            val download = downloadRepository.getDownloadById(id)
            if (download != null) {
                downloadRepository.deleteDownload(id)
                downloadRepository.deleteChunksForDownload(id)
                chapterRepository.updateChapterStatus(download.chapterId, Chapter.Status.NOT_DOWNLOADED)
            }
            // Check if queue is empty, if so cancel worker
            val remaining = downloadRepository.getDownloadsByStatus(DownloadStatus.DOWNLOADING) +
                            downloadRepository.getDownloadsByStatus(DownloadStatus.PENDING)
            if (remaining.isEmpty()) {
                workManager.cancelAllWorkByTag(DOWNLOAD_WORK_TAG)
            }
        }
    }

    /**
     * Enqueues the WorkManager task with constraints.
     */
    private fun triggerBackgroundWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .addTag(DOWNLOAD_WORK_TAG)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            DOWNLOAD_WORK_NAME,
            ExistingWorkPolicy.KEEP, // Keep existing to prevent interrupting currently running ones
            downloadWorkRequest
        )
    }

    companion object {
        const val DOWNLOAD_WORK_TAG = "FMD2_DOWNLOAD_WORK"
        const val DOWNLOAD_WORK_NAME = "FMD2_DOWNLOAD_JOB"
    }
}
