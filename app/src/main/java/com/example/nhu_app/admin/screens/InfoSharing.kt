package com.example.nhu_app.admin.screens

import android.app.DatePickerDialog
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun InfoSharing(navController: NavHostController) {
    var showNewsForm by remember { mutableStateOf(false) }
    var showScoreForm by remember { mutableStateOf(false) }
    var showImageForm by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {
        BoxItemIS("Post News") { showNewsForm = !showNewsForm }
        if (showNewsForm) {
            NewsForm { news ->
                db.collection("news").add(news)
                    .addOnSuccessListener { Log.d("Firestore", "News posted") }
                    .addOnFailureListener { e -> Log.e("Firestore", "Error posting news", e) }
                showNewsForm = false
            }
        }

        BoxItemIS("Post Score") { showScoreForm = !showScoreForm }
        if (showScoreForm) {
            ScoreForm { score ->
                db.collection("scores").add(score)
                    .addOnSuccessListener { Log.d("Firestore", "Score posted") }
                    .addOnFailureListener { e -> Log.e("Firestore", "Error posting score", e) }
                showScoreForm = false
            }
        }

        BoxItemIS("Highlights Image") { showImageForm = !showImageForm }
        if (showImageForm) {
            GameImageForm {
                showImageForm = false
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

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        OutlinedTextField(title, { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(description, { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5)

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
                    // Save the image to internal storage
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
                            // Submit the news item with the local file path
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

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
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

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
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

                // Save image to internal storage
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

                // Store path in Firestore
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

@Composable
fun BoxItemIS(label: String, onClick: () -> Unit) {
    Surface(
        tonalElevation = 4.dp, shadowElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() }
    ) {
        Box(
            Modifier.background(Color(0xFFE0E0E0)).height(60.dp).padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(label, fontSize = 16.sp, color = Color.Black)
        }
    }
}