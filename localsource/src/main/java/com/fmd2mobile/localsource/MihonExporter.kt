package com.fmd2mobile.localsource

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.fmd2mobile.core.model.Chapter
import com.fmd2mobile.core.model.Manga
import com.fmd2mobile.core.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Exporter class responsible for formatting downloads to follow Mihon's Local Source scheme.
 * Creates the cover.jpg from thumbnail, calls CbzCreator to pack chapters,
 * and notifies the system media scanner of new files.
 */
@Singleton
class MihonExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val client: OkHttpClient
) {

    /**
     * Exports a downloaded chapter into the Mihon/local/ directory.
     * Combines temporary pages into a CBZ and downloads/saves the cover.jpg.
     *
     * @param tempDir The folder containing downloaded page images.
     * @param manga The parent Manga metadata.
     * @param chapter The Chapter metadata.
     * @return The final CBZ File if successful, null otherwise.
     */
    suspend fun exportChapter(tempDir: File, manga: Manga, chapter: Chapter): File? = withContext(Dispatchers.IO) {
        val rootPath = settingsRepository.getDownloadLocation().first()
        
        // Sanitize manga title and chapter title for filenames
        val sanitizedMangaTitle = sanitizeFilename(manga.title)
        val sanitizedChapterTitle = sanitizeFilename(chapter.title)
        
        val mangaFolder = File(rootPath, sanitizedMangaTitle)
        if (!mangaFolder.exists()) {
            mangaFolder.mkdirs()
        }

        // 1. Export Cover if not already present
        val coverFile = File(mangaFolder, "cover.jpg")
        if (!coverFile.exists() && manga.thumbnailUrl.isNotEmpty()) {
            downloadCover(manga.thumbnailUrl, coverFile)
        }

        // 2. Export CBZ Chapter file
        val targetCbzFile = File(mangaFolder, "$sanitizedChapterTitle.cbz")
        val success = CbzCreator.createCbz(tempDir, targetCbzFile)

        if (success) {
            // 3. Scan file to notify OS/Mihon
            scanFile(targetCbzFile)
            if (coverFile.exists()) {
                scanFile(coverFile)
            }
            targetCbzFile
        } else {
            null
        }
    }

    /**
     * Downloads and writes manga cover to target file.
     */
    private fun downloadCover(url: String, dest: File) {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body
                    if (body != null) {
                        FileOutputStream(dest).use { fos ->
                            body.byteStream().copyTo(fos)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Triggers media scanner scan file intent to refresh Android media database.
     */
    private fun scanFile(file: File) {
        try {
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
                data = Uri.fromFile(file)
            }
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Sanitizes strings to prevent invalid filesystem character issues.
     */
    private fun sanitizeFilename(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_")
    }
}
