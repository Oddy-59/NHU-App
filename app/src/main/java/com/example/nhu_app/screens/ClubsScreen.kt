package com.example.nhu_app.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.nhu_app.R
import com.example.nhu_app.components.BottomNavigationItem
import DrawerContent
import com.example.nhu_app.models.Club
import com.example.nhu_app.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubsScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val clubs = remember { mutableStateListOf<Club>() }
    var expandedClubId by remember { mutableStateOf<String?>(null) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        db.collection("clubs")
            .get()
            .addOnSuccessListener { result ->
                clubs.clear()
                for (doc in result) {
                    val club = doc.toObject(Club::class.java)
                    clubs.add(club)
                }
            }
    }

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
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "CLUBS",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
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
                        scope.launch {
                            drawerState.open()
                        }
                    }
                }
            }
        ) { paddingValues ->
            if (clubs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Loading clubs...",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(clubs, key = { it.id }) { club ->
                        ClubCard(
                            club = club,
                            expanded = club.id == expandedClubId,
                            onClick = {
                                expandedClubId = if (expandedClubId == club.id) null else club.id
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClubCard(club: Club, expanded: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val painter = rememberAsyncImagePainter(model = Uri.parse(club.imageUrl))
            Image(
                painter = painter,
                contentDescription = "${club.name} Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = club.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = club.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

