package com.example.pyazlens.navigation

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")
    data object Language : Screen("language")

    data object UserDetails : Screen("user_details")

    data object Main : Screen("main")

    data object Home : Screen("home")

    data object Scan : Screen("scan")

    data object History : Screen("history")

    data object Insights : Screen("insights")

    data object Settings : Screen("settings")

    data object InspectionResult : Screen("inspection_result")

    data object Otp : Screen("otp")
}