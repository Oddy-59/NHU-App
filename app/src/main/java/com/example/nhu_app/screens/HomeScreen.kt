package com.example.nhu_app.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nhu_app.R
import com.example.nhu_app.components.BottomNavigationItem
import DrawerContent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.example.nhu_app.models.NewsItem
import com.example.nhu_app.models.ScoreItem
import com.example.nhu_app.models.GalleryItem
import com.example.nhu_app.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val newsItems = remember { mutableStateListOf<NewsItem>() }
    val scoreItems = remember { mutableStateListOf<ScoreItem>() }
    val galleryItems = remember { mutableStateListOf<GalleryItem>() }

    var isLoadingNews by remember { mutableStateOf(true) }
    var isLoadingScores by remember { mutableStateOf(true) }
    var isLoadingGallery by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("news")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { result ->
                newsItems.clear()
                for (doc in result) {
                    val item = doc.toObject(NewsItem::class.java)
                    newsItems.add(item)
                }
                isLoadingNews = false
            }

        db.collection("scores")
            .get()
            .addOnSuccessListener { result ->
                scoreItems.clear()
                for (doc in result) {
                    val item = doc.toObject(ScoreItem::class.java)
                    scoreItems.add(item)
                }
                isLoadingScores = false
            }

        db.collection("gallery")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                galleryItems.clear()
                for (doc in result) {
                    val item = doc.toObject(GalleryItem::class.java)
                    galleryItems.add(item)
                }
                isLoadingGallery = false
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.logo2), // Replace with your actual logo resource
                                    contentDescription = "NHU Logo",
                                    modifier = Modifier
                                        .size(47.dp)
                                        .padding(end = 5.dp),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary) // Match text color
                                )
                                Text(
                                    text = "NHU HOME",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    SectionHeader("Latest News")
                    Spacer(modifier = Modifier.height(7.dp))
                    when {
                        isLoadingNews -> {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                        newsItems.isEmpty() -> {
                            Text("No news available", style = MaterialTheme.typography.bodyMedium)
                        }
                        else -> {
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                newsItems.forEach { news ->
                                    Card(
                                        onClick = { navController.navigate(Screen.News.route) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(Modifier.padding(16.dp)) {
                                            Text(
                                                news.title,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                news.date,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    SectionHeader("Scores")
                    Spacer(modifier = Modifier.height(7.dp))
                    when {
                        isLoadingScores -> {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                        scoreItems.isEmpty() -> {
                            Text("No scores available", style = MaterialTheme.typography.bodyMedium)
                        }
                        else -> {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                scoreItems.forEach { score ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    ) {
                                        Column(Modifier.padding(16.dp)) {
                                            Text(
                                                "${score.teams} | ${score.score}",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Text(
                                                score.date,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    SectionHeader("Top Teams")
                    Spacer(modifier = Modifier.height(7.dp))
                    TeamsSection()
                }

                item {
                    SectionHeader("Highlights Gallery")
                    Spacer(modifier = Modifier.height(7.dp))
                    when {
                        isLoadingGallery -> {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                        galleryItems.isEmpty() -> {
                            Text("No gallery images available", style = MaterialTheme.typography.bodyMedium)
                        }
                        else -> {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(galleryItems) { image ->
                                    val file = File(image.imagePath)
                                    val bitmap = remember(file.path) {
                                        if (file.exists()) BitmapFactory.decodeFile(file.path)?.asImageBitmap() else null
                                    }

                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.size(160.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.Transparent
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                    ) {
                                        bitmap?.let {
                                            Image(
                                                bitmap = it,
                                                contentDescription = image.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(12.dp))
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

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun TeamsSection() {
    val teams = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("teams")
            .limit(7) // Fetch up to 20 teams
            .get()
            .addOnSuccessListener { result ->
                val allTeams = result.mapNotNull { it.getString("teamName") }.shuffled()
                teams.clear()
                teams.addAll(allTeams.take(4)) // Show only 5 random teams
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    when {
        isLoading -> {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        teams.isEmpty() -> {
            Text("No teams available", style = MaterialTheme.typography.bodyMedium)
        }
        else -> {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(teams) { team ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
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
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}
