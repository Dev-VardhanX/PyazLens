package com.example.pyazlens.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pyazlens.navigation.Screen
import com.example.pyazlens.ui.components.BottomNavigationBar
import com.example.pyazlens.ui.home.HomeScreen

@Composable
fun MainScaffold() {

    // Navigation controller used only for the main app
    val mainNavController = rememberNavController()

    // Observe the current screen
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route
            ?: Screen.Home.route

    Scaffold(
        bottomBar = {

            BottomNavigationBar(
                currentRoute = currentRoute,
                onItemClick = { route ->

                    mainNavController.navigate(route) {

                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->

        NavHost(
            navController = mainNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            composable(Screen.Home.route) {

                HomeScreen(
                    onInspectClick = {
                        // Camera will be added later
                    }
                )
            }

            composable(Screen.History.route) {

                // HistoryScreen will be added later
            }

            composable(Screen.Stats.route) {

                // StatsScreen will be added later
            }

            composable(Screen.Settings.route) {

                // SettingsScreen will be added later
            }
        }
    }
}