package com.example.nhu_app.admin.screens

import DrawerContent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.compose.rememberNavController
import com.example.nhu_app.R
import com.example.nhu_app.components.BottomNavigationItem
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import com.example.nhu_app.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    // Prevent back navigation from Admin screen
    BackHandler {}

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                drawerState = drawerState,
                scope = scope,
                navController = navController
            )
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
                            Text(
                                text = "Dashboard",
                                color = colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.primary
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = colorScheme.primary,
                    modifier = Modifier.height(70.dp)
                ) {
                    BottomNavigationItem(R.drawable.ic_home, "Home") {
                        navController.navigate(Screen.Home.route)
                    }
                    BottomNavigationItem(R.drawable.ic_events, "Events") {
                        navController.navigate(Screen.Events.route)
                    }
                    BottomNavigationItem(R.drawable.ic_news, "News") {
                        navController.navigate(Screen.News.route)
                    }
                    BottomNavigationItem(R.drawable.ic_clubs, "Clubs") {
                        navController.navigate(Screen.Clubs.route)
                    }
                    BottomNavigationItem(R.drawable.ic_menu, "More") {
                        scope.launch { drawerState.open() }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = "Manage Hockey Info",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BoxItemAS(R.drawable.ic_team, "Team Registration") {
                        navController.navigate(Screen.TeamReg.route)
                    }
                    BoxItemAS(R.drawable.ic_player, "Player Registration") {
                        navController.navigate(Screen.PlayerReg.route)
                    }
                    BoxItemAS(R.drawable.ic_event, "Event Entries") {
                        navController.navigate(Screen.Entry.route)
                    }
                    BoxItemAS(R.drawable.ic_info, "Info Sharing") {
                        navController.navigate(Screen.Info.route)
                    }
                    BoxItemAS(R.drawable.ic_club, "Add A Club") {
                        navController.navigate(Screen.Club.route)
                    }
                }

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) // Clears the back stack
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Text("Logout", color = colorScheme.onError)
                }
            }
        }
    }
}


@Composable
fun BoxItemAS(image: Int, label: String, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.02f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scaleAnim"
    )

    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
            onClick()
        }
    }

    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 3.dp,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 8.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { isPressed = true },
        color = colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier
                .height(70.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = label,
                modifier = Modifier
                    .size(36.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = label,
                fontSize = 16.sp,
                color = colorScheme.onSecondaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdminScreen() {
    val navController = rememberNavController()
    AdminScreen(navController)
}