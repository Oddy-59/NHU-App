package com.example.nhu_app.admin.screens

import android.app.DatePickerDialog
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

data class NewsItem(val imageUri: String?, val title: String, val description: String, val date: String)
data class Score(val teams: String, val score: String, val date: String)
data class GameImage(val imageUri: String?, val title: String)

val db = FirebaseFirestore.getInstance()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoSharing(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    var showNewsForm by remember { mutableStateOf(false) }
    var showScoreForm by remember { mutableStateOf(false) }
    var showImageForm by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Share News, Scores, and Highlights",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            item {
                BoxItemIS("Post News") {
                    showNewsForm = !showNewsForm
                    showScoreForm = false
                    showImageForm = false
                }
            }
            if (showNewsForm) {
                item {
                    NewsForm { news ->
                        db.collection("news").add(news)
                            .addOnSuccessListener {
                                Toast.makeText(context, "News posted successfully!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to post news.", Toast.LENGTH_SHORT).show()
                            }
                        showNewsForm = false
                    }
                }
            }

            item {
                BoxItemIS("Post Score") {
                    showScoreForm = !showScoreForm
                    showNewsForm = false
                    showImageForm = false
                }
            }
            if (showScoreForm) {
                item {
                    ScoreForm { score ->
                        db.collection("scores").add(score)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Score posted successfully!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to post score.", Toast.LENGTH_SHORT).show()
                            }
                        showScoreForm = false
                    }
                }
            }

            item {
                BoxItemIS("Highlights Image") {
                    showImageForm = !showImageForm
                    showNewsForm = false
                    showScoreForm = false
                }
            }
            if (showImageForm) {
                item {
                    GameImageForm {
                        showImageForm = false
                    }
                }
            }
        }
    }
}

@Composable
fun NewsForm(onSubmit: (NewsItem) -> Unit) {
    var title by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }
    var date by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageSelected by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePicker = {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                date = format.format(calendar.time)
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        imageSelected = it != null
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(title, { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                description, { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Button(onClick = datePicker, modifier = Modifier.padding(top = 8.dp)) {
                Text(if (date.isEmpty()) "Select Date" else "Date: $date")
            }

            Button(onClick = { imagePicker.launch("image/*") }, modifier = Modifier.padding(top = 8.dp)) {
                Text(if (imageSelected) "Image Selected" else "Select Image")
            }

            if (error.isNotEmpty()) Text(error, color = Color.Red, fontSize = 14.sp)

            Button(
                onClick = {
                    if (title.text.isBlank() || description.text.isBlank() || date.isEmpty()) {
                        error = "All fields are required."
                    } else {
                        imageUri?.let { uri ->
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val fileName = "news_image_${System.currentTimeMillis()}.jpg"
                            val file = File(context.filesDir, fileName)

                            try {
                                inputStream?.use { input ->
                                    FileOutputStream(file).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                val filePath = file.absolutePath
                                onSubmit(NewsItem(filePath, title.text, description.text, date))
                            } catch (e: Exception) {
                                error = "Error saving image: ${e.message}"
                            }
                        } ?: run {
                            error = "Please select an image."
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End).padding(top = 12.dp)
            ) {
                Text("Post News")
            }
        }
    }
}

@Composable
fun ScoreForm(onSubmit: (Score) -> Unit) {
    var teams by remember { mutableStateOf(TextFieldValue()) }
    var score by remember { mutableStateOf(TextFieldValue()) }
    var date by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePicker = {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                date = format.format(calendar.time)
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(teams, { teams = it }, label = { Text("Teams") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(score, { score = it }, label = { Text("Score") }, modifier = Modifier.fillMaxWidth())

            Button(onClick = datePicker, modifier = Modifier.padding(top = 8.dp)) {
                Text(if (date.isEmpty()) "Select Date" else "Date: $date")
            }

            if (error.isNotEmpty()) Text(error, color = Color.Red, fontSize = 14.sp)

            Button(
                onClick = {
                    if (teams.text.isBlank() || score.text.isBlank() || date.isEmpty()) {
                        error = "All fields are required."
                    } else {
                        onSubmit(Score(teams.text, score.text, date))
                    }
                },
                modifier = Modifier.align(Alignment.End).padding(top = 12.dp)
            ) {
                Text("Post Score")
            }
        }
    }
}

@Composable
fun GameImageForm(onSubmitDone: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var title by remember { mutableStateOf(TextFieldValue()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageSelected by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        imageSelected = it != null
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { imagePicker.launch("image/*") }) {
                Text(if (imageSelected) "Image Selected" else "Select Image")
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red)
            }

            Button(
                onClick = onClick@{
                    if (title.text.isBlank() || imageUri == null) {
                        errorMessage = "Please enter a title and select an image."
                        return@onClick
                    }

                    isSubmitting = true
                    errorMessage = ""

                    val fileName = "game_image_${System.currentTimeMillis()}.jpg"
                    val file = File(context.filesDir, fileName)

                    try {
                        val inputStream = context.contentResolver.openInputStream(imageUri!!)
                        val outputStream = FileOutputStream(file)
                        inputStream?.copyTo(outputStream)
                        inputStream?.close()
                        outputStream.close()
                    } catch (e: IOException) {
                        errorMessage = "Error saving image: ${e.message}"
                        isSubmitting = false
                        return@onClick
                    }

                    val imageData = hashMapOf(
                        "title" to title.text,
                        "imagePath" to file.absolutePath,
                        "timestamp" to System.currentTimeMillis().toString()
                    )

                    db.collection("gallery")
                        .add(imageData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Highlight shared!", Toast.LENGTH_SHORT).show()
                            onSubmitDone()
                        }
                        .addOnFailureListener {
                            errorMessage = "Failed to upload: ${it.message}"
                        }
                        .addOnCompleteListener {
                            isSubmitting = false
                        }
                },
                enabled = !isSubmitting,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (isSubmitting) "Sharing..." else "Share Image")
            }
        }
    }
}

@Composable
fun BoxItemIS(label: String, onClick: () -> Unit) {
    Surface(
        tonalElevation = 2.dp, shadowElevation = 4.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(vertical = 16.dp, horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = label,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}