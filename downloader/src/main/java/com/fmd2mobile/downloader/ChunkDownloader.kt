package com.fmd2mobile.downloader

import com.fmd2mobile.core.model.DownloadChunk
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.RandomAccessFile
import java.io.IOException
import java.util.concurrent.atomic.AtomicLong

/**
 * Downloads a single file using parallel HTTP Range requests (chunked downloading).
 * Writes bytes directly to specified positions in a RandomAccessFile.
 * Supports pausing, resuming from database progress, and tracking download speed.
 */
class ChunkDownloader(
    private val client: OkHttpClient,
    private val url: String,
    private val outputFile: File,
    private val downloadId: Long,
    private val initialChunks: List<DownloadChunk>,
    private val onProgress: (chunkIndex: Int, bytesDownloaded: Long) -> Unit,
    private val onTotalBytesCalculated: (totalBytes: Long) -> Unit
) {
    private val activeJobs = mutableListOf<Job>()
    private val totalBytesDownloaded = AtomicLong(0)

    suspend fun download(scope: CoroutineScope, numChunks: Int = 4): Boolean = withContext(Dispatchers.IO) {
        val totalLength = getContentLength(url)
        onTotalBytesCalculated(totalLength)

        if (totalLength <= 0) {
            // Cannot use Range download without knowing total length; fallback to single stream
            return@withContext downloadSingleStream()
        }

        // Initialize output file size
        RandomAccessFile(outputFile, "rw").use { raf ->
            raf.setLength(totalLength)
        }

        val chunksToUse = if (initialChunks.isNotEmpty()) {
            initialChunks
        } else {
            // Calculate chunk size and boundaries
            val chunkSize = totalLength / numChunks
            List(numChunks) { index ->
                val start = index * chunkSize
                val end = if (index == numChunks - 1) totalLength - 1 else (index + 1) * chunkSize - 1
                DownloadChunk(
                    downloadId = downloadId,
                    chunkIndex = index,
                    startByte = start,
                    endByte = end,
                    bytesDownloaded = 0
                )
            }
        }

        // Initialize total downloaded bytes accumulator
        totalBytesDownloaded.set(chunksToUse.sumOf { it.bytesDownloaded })

        val deferreds = chunksToUse.map { chunk ->
            scope.async(Dispatchers.IO) {
                downloadChunkWithRetry(chunk)
            }
        }

        activeJobs.addAll(deferreds)
        val results = deferreds.awaitAll()
        return@withContext results.all { it }
    }

    /**
     * Gets content length of the remote file using a HEAD request.
     */
    private fun getContentLength(url: String): Long {
        return try {
            val request = Request.Builder().url(url).head().build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.header("Content-Length")?.toLongOrNull() ?: -1L
                } else {
                    -1L
                }
            }
        } catch (e: Exception) {
            -1L
        }
    }

    /**
     * Fallback for URLs that do not support Range requests.
     */
    private fun downloadSingleStream(): Boolean {
        return try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return false
                val body = response.body ?: return false
                outputFile.outputStream().use { fos ->
                    body.byteStream().use { inputStream ->
                        val buffer = ByteArray(8192)
                        var bytes: Int
                        while (inputStream.read(buffer).also { bytes = it } >= 0) {
                            fos.write(buffer, 0, bytes)
                            val current = totalBytesDownloaded.addAndGet(bytes.toLong())
                            onProgress(0, current)
                        }
                    }
                }
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Downloads a single chunk with retry and exponential backoff.
     */
    private suspend fun downloadChunkWithRetry(chunk: DownloadChunk): Boolean {
        var retries = 0
        val maxRetries = 3
        var currentStart = chunk.startByte + chunk.bytesDownloaded
        var chunkBytesDownloaded = chunk.bytesDownloaded

        while (retries <= maxRetries) {
            if (currentStart > chunk.endByte) {
                // Chunk is already complete
                return true
            }

            try {
                val request = Request.Builder()
                    .url(url)
                    .header("Range", "bytes=$currentStart-${chunk.endByte}")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.code != 206 && response.code != 200) {
                        throw IOException("Server returned HTTP ${response.code}")
                    }

                    val body = response.body ?: throw IOException("Empty response body")
                    RandomAccessFile(outputFile, "rw").use { raf ->
                        raf.seek(currentStart)
                        val inputStream = body.byteStream()
                        val buffer = ByteArray(8192)
                        var bytesRead: Int

                        while (inputStream.read(buffer).also { bytesRead = it } >= 0) {
                            yield() // Support cooperative cancellation when paused
                            raf.write(buffer, 0, bytesRead)
                            currentStart += bytesRead
                            chunkBytesDownloaded += bytesRead
                            totalBytesDownloaded.addAndGet(bytesRead.toLong())
                            onProgress(chunk.chunkIndex, chunkBytesDownloaded)
                        }
                    }
                }
                return true // Success
            } catch (e: CancellationException) {
                throw e // Propagate coroutine cancellation immediately
            } catch (e: Exception) {
                retries++
                if (retries > maxRetries) {
                    return false
                }
                // Exponential backoff: 1s, 2s, 4s
                val delayTime = (1 shl (retries - 1)) * 1000L
                delay(delayTime)
            }
        }
        return false
    }

    fun cancel() {
        activeJobs.forEach { it.cancel() }
    }
}
