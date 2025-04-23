package com.example.nhu_app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhu_app.R
import com.example.nhu_app.components.BottomNavigationItem
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.example.nhu_app.navigation.Screen
import kotlinx.coroutines.launch
import com.example.nhu_app.components.DrawerContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(drawerState, scope)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Namibia Hockey Union", color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.DarkGray)
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = Color.DarkGray,
                    modifier = Modifier.height(70.dp)
                ) {
                    BottomNavigationItem(R.drawable.ic_home, "Home") {
                        navController.navigate(Screen.Home.route)
                    }
                    BottomNavigationItem(R.drawable.ic_fixtures, "Fixtures") {
                        navController.navigate(Screen.Fixtures.route)
                    }
                    BottomNavigationItem(R.drawable.ic_rankings, "Rankings") {
                        navController.navigate(Screen.Rankings.route)
                    }
                    BottomNavigationItem(R.drawable.ic_teams, "Teams") {
                        navController.navigate(Screen.Teams.route)
                    }
                    BottomNavigationItem(R.drawable.ic_menu, "More") {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    SectionHeader("Latest News")
                    NewsSection()
                }

                item {
                    SectionHeader("Recent Scores")
                    ScoresSection()
                }

                item {
                    SectionHeader("Gallery Highlights")
                    GallerySection()
                }

                item {
                    SectionHeader("Featured Teams")
                    TeamsSection()
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun NewsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            "Namibia defeats SA in thrilling semi-final",
            "NHU unveils new youth program",
            "Coach shares tactics behind big win"
        ).forEach { headline ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = headline,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ScoresSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            "Namibia 3 - 1 South Africa",
            "Namibia 2 - 2 Kenya",
            "Namibia 4 - 0 Zimbabwe"
        ).forEach { score ->
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
            ) {
                Text(
                    text = score,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun GallerySection() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(5) {
            Card(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(120.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_gallery),
                    contentDescription = "Gallery Image",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun TeamsSection() {
    val teams = listOf("Windhoek Warriors", "Desert Hawks", "Coastal Strikers")

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(teams) { team ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD8F3DC)),
                modifier = Modifier
                    .width(150.dp)
                    .height(80.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = team,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Home Screen")
@Composable
fun PreviewHomeScreen() {
    val navController = rememberNavController()
    HomeScreen(navController)
}
