package com.example.nhu_app.navigation

sealed class Screen(val route: String) {
    object Login : Screen("Login")
    object Home : Screen("home")
    object Events : Screen("events")
    object News : Screen("news")
    object Clubs : Screen("clubs")
    object Admin : Screen("admin")
    object More : Screen("more")

    // Admin Functional Screens
    object TeamReg : Screen("team_reg")
    object PlayerReg : Screen("player_reg")
    object Entry : Screen("entries")
    object Info : Screen("info")
    object Club : Screen("club")
}
