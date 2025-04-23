package com.example.nhu_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nhu_app.navigation.Screen
import com.example.nhu_app.admin.screens.AdminScreen
import com.example.nhu_app.admin.screens.TeamRegistrationScreen
import com.example.nhu_app.admin.screens.AdminInfoScreen
import com.example.nhu_app.screens.FixturesScreen
import com.example.nhu_app.screens.RankingsScreen
import com.example.nhu_app.screens.TeamsScreen
import com.example.nhu_app.screens.HomeScreen
import com.example.nhu_app.screens.SignUpScreen
import com.example.nhu_app.screens.StartUpScreen
import com.example.nhu_app.admin.screens.EventsScreen
import com.example.nhu_app.admin.screens.PlayerRegistrationScreen
import com.example.nhu_app.ui.theme.NHUAppTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NHUAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    NHUApp()
                }
            }
        }
    }
}

@Composable
fun NHUApp() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.StartUp.route) {
        composable(Screen.StartUp.route) { StartUpScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }
        composable(Screen.Admin.route) { AdminScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Fixtures.route) { FixturesScreen(navController) }
        composable(Screen.Rankings.route) { RankingsScreen(navController) }
        composable(Screen.Teams.route) { TeamsScreen(navController) }

        // New Admin Screens
        composable(Screen.TeamRegistration.route) { TeamRegistrationScreen() }
        composable(Screen.PlayerRegistration.route) { PlayerRegistrationScreen() }
        composable(Screen.Events.route) { EventsScreen() }
        composable(Screen.AdminInfo.route) { AdminInfoScreen() }
    }
}



