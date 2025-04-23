package com.example.nhu_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nhu_app.R
import com.example.nhu_app.components.BottomNavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixturesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Fixtures", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.DarkGray
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.DarkGray,
                modifier = Modifier.height(70.dp)
            ) {
                BottomNavigationItem(R.drawable.ic_home, "Home") {
                    navController.navigate("home")
                }
                BottomNavigationItem(R.drawable.ic_fixtures, "Fixtures") {
                    navController.navigate("fixtures")
                }
                BottomNavigationItem(R.drawable.ic_rankings, "Rankings") {
                    navController.navigate("rankings")
                }
                BottomNavigationItem(R.drawable.ic_teams, "Teams") {
                    navController.navigate("teams")
                }
                BottomNavigationItem(R.drawable.ic_menu, "More") {
                    // TODO: Handle side drawer or bottom sheet
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Fixtures Screen Content")
        }
    }
}
