package com.example.pyazlens.ui.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pyazlens.navigation.Screen
import com.example.pyazlens.ui.components.BottomNavigationBar
import com.example.pyazlens.ui.history.HistoryScreen
import com.example.pyazlens.ui.home.HomeScreen
import com.example.pyazlens.ui.scan.ScanScreen
import com.example.pyazlens.ui.settings.SettingsScreen

@Composable
fun MainScaffold() {

    // Navigation controller used only for the main app
    val mainNavController = rememberNavController()

    // Observe the current screen
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route
            ?: Screen.Home.route

    var uploadedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {
                uploadedImageUri = uri

                mainNavController.navigate(Screen.Scan.route)
            }
        }
    Scaffold(
        bottomBar = {

            BottomNavigationBar(
                currentRoute = currentRoute,
                onItemClick = { route ->

                    // Clear any previously selected gallery image
                    // when returning to Home
                    if (route == Screen.Home.route) {
                        uploadedImageUri = null
                    }

                    mainNavController.navigate(route) {
                        popUpTo(Screen.Home.route) {
                            saveState = false
                        }
                        launchSingleTop = true
                        restoreState = false
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

                    // Camera
                    onInspectClick = {
                        uploadedImageUri = null

                        mainNavController.navigate(
                            Screen.Scan.route
                        )
                    },

                    // Gallery
                    onUploadClick = {
                        galleryLauncher.launch("image/*")
                    }
                )
            }

            // 2. SCAN (Camera & Image Processing)
            composable(Screen.Scan.route) {

                ScanScreen(
                    initialImageUri = uploadedImageUri
                )
            }

            // 3. INSPECTION RESULT SCREEN
            composable(Screen.InspectionResult.route) {

            }

            // 4. HISTORY
            composable(Screen.History.route) {
                HistoryScreen(
                    onRecordClick = {},
                    onDeleteRecord = {}
                )
            }

            // 5. INSIGHTS
            composable(Screen.Insights.route) {

            }

            // 6. SETTINGS
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}