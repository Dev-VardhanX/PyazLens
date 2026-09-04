package com.example.pyazlens.navigation

sealed class Screen(val route: String) {

    data object Language : Screen("language")

    data object UserDetails : Screen("user_details")

    data object Main : Screen("main")

    data object Home : Screen("home")

    data object History : Screen("history")

    data object Stats : Screen("stats")

    data object Settings : Screen("settings")
}