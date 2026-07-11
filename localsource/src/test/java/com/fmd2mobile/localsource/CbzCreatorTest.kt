package com.fmd2mobile.localsource

import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * Unit tests verifying CBZ file archival logic.
 */
class CbzCreatorTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun createCbz_packsImagesSuccessfully() {
        // Arrange: Create mock pages in temporary folder
        val sourceDir = tempFolder.newFolder("pages")
        val page1 = File(sourceDir, "p1.jpg").apply { writeText("mock bytes page 1") }
        val page2 = File(sourceDir, "p2.jpg").apply { writeText("mock bytes page 2") }

        val targetCbz = File(tempFolder.root, "chapter1.cbz")

        // Act: Create CBZ zip
        val success = CbzCreator.createCbz(sourceDir, targetCbz)

        // Assert: Verify success status and archive existence
        assertTrue(success)
        assertTrue(targetCbz.exists())
        assertTrue(targetCbz.length() > 0)
    }
}
