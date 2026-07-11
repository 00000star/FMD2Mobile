package com.fmd2mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fmd2mobile.ui.screens.*

/**
 * Setup navigation routing host mapping destinations to their composable views.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Library.route,
        modifier = modifier
    ) {
        composable(Screen.Library.route) {
            LibraryScreen(
                onNavigateToDetails = { mangaId ->
                    navController.navigate(Screen.MangaDetails.createRoute(mangaId))
                }
            )
        }
        
        composable(Screen.Browse.route) {
            BrowseScreen(
                onNavigateToDetails = { mangaId ->
                    navController.navigate(Screen.MangaDetails.createRoute(mangaId))
                }
            )
        }
        
        composable(Screen.Downloads.route) {
            DownloadsScreen()
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        
        composable(
            route = Screen.MangaDetails.route,
            arguments = listOf(navArgument("mangaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val mangaId = backStackEntry.arguments?.getLong("mangaId") ?: 0L
            MangaDetailsScreen(
                mangaId = mangaId,
                onNavigateToReader = { chapterId ->
                    navController.navigate(Screen.Reader.createRoute(chapterId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.Reader.route,
            arguments = listOf(navArgument("chapterId") { type = NavType.LongType })
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getLong("chapterId") ?: 0L
            ReaderScreen(
                chapterId = chapterId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
