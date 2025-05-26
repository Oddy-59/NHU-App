package com.example.nhu_app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhu_app.R
import com.example.nhu_app.components.BottomNavigationItem
import DrawerContent
import com.example.nhu_app.models.Event
import com.example.nhu_app.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val eventItems = remember { mutableStateListOf<Event>() }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch events from Firestore
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("events")
            .get()
            .addOnSuccessListener { result ->
                eventItems.clear()
                for (doc in result) {
                    val event = doc.toObject(Event::class.java)
                    eventItems.add(event)
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(drawerState = drawerState, scope = scope, navController = navController)
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
                                text = "EVENTS",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
                )
            },

            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primary,
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Upcoming Events:",
                        fontSize = 20.sp, // same as Latest News header font size
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground // matches Latest News header color
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                item {
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        eventItems.isEmpty() -> {
                            Text(
                                "No events available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        }

                        else -> {
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                eventItems.forEach { event ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    ) {
                                        Column(Modifier.padding(16.dp)) {
                                            Text(
                                                "${event.title} - ${event.venue}",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Spacer(Modifier.height(3.dp))
                                            Text(
                                                "Date: ${event.date}",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                            )
                                            Spacer(Modifier.height(1.dp))
                                            Text(
                                                "Time: ${event.time}",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
