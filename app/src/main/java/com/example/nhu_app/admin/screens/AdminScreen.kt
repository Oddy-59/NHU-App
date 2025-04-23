package com.example.nhu_app.admin.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.compose.rememberNavController
import com.example.nhu_app.R
import com.example.nhu_app.components.BottomNavigationItem
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import com.example.nhu_app.navigation.Screen
import com.example.nhu_app.components.DrawerContent
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(drawerState, scope) // Use the DrawerContent composable
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
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "NHU Logo",
                                modifier = Modifier.size(60.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
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
                        scope.launch { drawerState.open() }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BoxItem(R.drawable.ic_team, "Team Registration") {
                            navController.navigate(Screen.TeamRegistration.route)
                        }
                        BoxItem(R.drawable.ic_player, "Player Registration") {
                            navController.navigate(Screen.PlayerRegistration.route)
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BoxItem(R.drawable.ic_event, "Events") {
                            navController.navigate(Screen.Events.route)
                        }
                        BoxItem(R.drawable.ic_info, "Information") {
                            navController.navigate(Screen.AdminInfo.route)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoxItem(image: Int, label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.1f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "boxBounce"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
            onClick()
        }
    }

    Column(
        modifier = Modifier
            .padding(15.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.LightGray)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .size(140.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = label,
            modifier = Modifier.size(55.dp)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true, name = "Admin Screen")
@Composable
fun PreviewAdminScreen() {
    val navController = rememberNavController()
    AdminScreen(navController)
}
