package com.fmd2mobile.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fmd2mobile.navigation.NavGraph
import com.fmd2mobile.navigation.Screen

/**
 * Main application screen composing the scaffolding layout with bottom navigation bar and navigation graph content.
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Library,
        Screen.Browse,
        Screen.Downloads,
        Screen.Settings
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Check if current route is reader, if so hide the bottom bar
            val showBottomBar = items.any { it.route == currentDestination?.route }
            
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFF0F172A), // Slate 900 matching main backgrounds
                    contentColor = Color.White
                ) {
                    items.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFA855F7), // Purple Accent
                                unselectedIconColor = Color.Gray,
                                selectedTextColor = Color(0xFFA855F7),
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0xFF1E293B) // Dark indicator background
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
