package com.example.nhu_app.navigation

sealed class Screen(val route: String) {
    object StartUp : Screen("startup")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Fixtures : Screen("fixtures")
    object Rankings : Screen("rankings")
    object Teams : Screen("teams")
    object Admin : Screen("admin")
    object More : Screen("more")

    // Admin Functional Screens
    object TeamRegistration : Screen("team_registration")
    object PlayerRegistration : Screen("player_registration")
    object Events : Screen("events")
    object AdminInfo : Screen("admin_info")
}
