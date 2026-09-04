package com.example.pyazlens.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pyazlens.ui.home.HomeScreen
import com.example.pyazlens.ui.language.LanguageScreen
import com.example.pyazlens.ui.main.MainScaffold
import com.example.pyazlens.ui.userdetails.UserDetailsScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Language.route
    ) {

        // LANGUAGE

        composable(Screen.Language.route) {

            LanguageScreen(
                onContinue = {

                    navController.navigate(
                        Screen.UserDetails.route
                    )
                }
            )
        }

        // USER DETAILS

        composable(Screen.UserDetails.route) {

            UserDetailsScreen(

                onBack = {
                    navController.popBackStack()
                },

                onContinue = { _, _, _ ->

                    navController.navigate(
                        Screen.Home.route
                    ) {
                        popUpTo(
                            Screen.Language.route
                        ) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // MAIN APPLICATION

        composable(Screen.Home.route) {

            MainScaffold()
        }
    }
}