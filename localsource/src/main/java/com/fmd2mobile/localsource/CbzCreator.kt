package com.fmd2mobile.localsource

import java.io.File
import java.io.FileOutputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Utility class for creating CBZ (Comic Book Zip) archives.
 * Packs files sequentially with STORED method (no compression) for optimal reader performance.
 */
object CbzCreator {

    /**
     * Packs all files in sourceDir into targetCbzFile.
     * Renames files to sequential pages (001.jpg, 002.jpg...) and sets STORED ZIP compression.
     *
     * @param sourceDir The directory containing page image files.
     * @param targetCbzFile The output CBZ file to write to.
     * @return True if successful, false otherwise.
     */
    fun createCbz(sourceDir: File, targetCbzFile: File): Boolean {
        if (!sourceDir.exists() || !sourceDir.isDirectory) return false
        val files = sourceDir.listFiles()?.sortedBy { it.name } ?: return false

        return try {
            if (targetCbzFile.exists()) {
                targetCbzFile.delete()
            }
            targetCbzFile.parentFile?.mkdirs()

            ZipOutputStream(FileOutputStream(targetCbzFile)).use { zos ->
                // Set STORED method on stream (no compression)
                zos.setMethod(ZipOutputStream.STORED)

                files.forEachIndexed { index, file ->
                    val extension = file.extension.ifEmpty { "jpg" }
                    val entryName = String.format("%03d.%s", index + 1, extension)
                    val bytes = file.readBytes()

                    // Calculate CRC32 checksum for STORED entry
                    val crc = CRC32().apply {
                        update(bytes)
                    }

                    val entry = ZipEntry(entryName).apply {
                        method = ZipEntry.STORED
                        size = bytes.size.toLong()
                        compressedSize = bytes.size.toLong()
                        setCrc(crc.value)
                    }

                    zos.putNextEntry(entry)
                    zos.write(bytes)
                    zos.closeEntry()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
