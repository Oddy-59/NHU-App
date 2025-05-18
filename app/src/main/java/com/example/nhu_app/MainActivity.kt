package com.example.nhu_app

import  android.os.Bundle
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
import com.example.nhu_app.admin.screens.Club
import com.example.nhu_app.admin.screens.Entries
import com.example.nhu_app.admin.screens.InfoSharing
import com.example.nhu_app.admin.screens.PlayerReg
import com.example.nhu_app.admin.screens.TeamReg
import com.example.nhu_app.screens.ClubsScreen
import com.example.nhu_app.screens.HomeScreen
import com.example.nhu_app.screens.LoginScreen
import com.example.nhu_app.screens.EventsScreen
import com.example.nhu_app.ui.theme.NHUAppTheme
import com.example.nhu_app.screens.NewsScreen
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth

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

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.News.route) { NewsScreen(navController) }
        composable(Screen.Events.route) { EventsScreen(navController) }
        composable(Screen.Clubs.route) { ClubsScreen(navController) }

        composable(Screen.Admin.route) { AdminScreen(navController) }
        composable(Screen.TeamReg.route) { TeamReg(navController) }
        composable(Screen.PlayerReg.route) { PlayerReg(navController) }
        composable(Screen.Entry.route) { Entries(navController) }
        composable(Screen.Info.route) { InfoSharing(navController) }
        composable(Screen.Club.route) { Club(navController) }
    }

}



