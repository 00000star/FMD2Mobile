package com.fmd2mobile.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing destinations in the application navigation graph.
 *
 * @param route The unique route string for navigation.
 * @param title The UI string resource ID or label representing the screen.
 * @param icon The vector icon representation of this destination.
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Library : Screen("library", "Library", Icons.Default.Book)
    object Browse : Screen("browse", "Browse", Icons.Default.Explore)
    object Downloads : Screen("downloads", "Downloads", Icons.Default.Download)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    
    // Reader screen path including details
    object Reader : Screen("reader/{chapterId}", "Reader", Icons.Default.Book) {
        fun createRoute(chapterId: Long) = "reader/$chapterId"
    }

    // Manga details screen
    object MangaDetails : Screen("manga/{mangaId}", "Manga Details", Icons.Default.Book) {
        fun createRoute(mangaId: Long) = "manga/$mangaId"
    }
}
