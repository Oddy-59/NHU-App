package com.example.nhu_app.admin.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

data class Club(
    val id: String = "",
    val name: String = "",
    val coach: String = "",
    val description: String = "",
    val contact: String = "",
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Club(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    val clubList = remember { mutableStateListOf<Club>() }

    var showAdd by remember { mutableStateOf(false) }
    var showView by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }

    var selectedEditClub by remember { mutableStateOf<Club?>(null) }
    var selectedDeleteClub by remember { mutableStateOf<Club?>(null) }

    LaunchedEffect(Unit) {
        db.collection("clubs").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                clubList.clear()
                for (doc in snapshot.documents) {
                    doc.toObject(Club::class.java)?.let { clubList.add(it) }
                }
            }
        }
    }

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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Manage Hockey Info",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
                )
            }

            item {
                BoxItem("Add Club") {
                    showAdd = !showAdd
                    showView = false
                    showDelete = false
                    selectedEditClub = null
                }
                if (showAdd) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        AddOrEditClubForm(
                            onSubmit = { club ->
                                val duplicate = clubList.any {
                                    it.name.equals(club.name, true) && it.id != club.id
                                }
                                if (duplicate) {
                                    Toast.makeText(context, "Club already exists", Toast.LENGTH_SHORT).show()
                                } else {
                                    db.collection("clubs").document(club.id).set(club)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Club added", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        )
                    }
                }
            }

            item {
                BoxItem("View Registered Clubs") {
                    showView = !showView
                    showAdd = false
                    showDelete = false
                    selectedEditClub = null
                }
            }

            if (showView) {
                items(clubList) { club ->
                    val isSelected = selectedEditClub?.id == club.id
                    ClubListItem(
                        club = club,
                        highlight = false,
                        onClick = {
                            selectedEditClub = if (isSelected) null else club
                        }
                    )
                }

                selectedEditClub?.let { club ->
                    item {
                        AddOrEditClubForm(club = club) { updated ->
                            val duplicate = clubList.any {
                                it.name.equals(updated.name, true) && it.id != updated.id
                            }
                            if (duplicate) {
                                Toast.makeText(context, "Club already exists", Toast.LENGTH_SHORT).show()
                            } else {
                                db.collection("clubs").document(updated.id).set(updated)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Club updated", Toast.LENGTH_SHORT).show()
                                        selectedEditClub = null
                                    }
                            }
                        }
                    }
                }
            }

            item {
                BoxItem("Delete Club") {
                    showDelete = !showDelete
                    showAdd = false
                    showView = false
                    selectedEditClub = null
                }
            }

            if (showDelete) {
                items(clubList) { club ->
                    val isSelected = selectedDeleteClub?.id == club.id
                    ClubListItem(
                        club = club,
                        highlight = isSelected,
                        onClick = {
                            selectedDeleteClub = if (isSelected) null else club
                        }
                    )
                }

                selectedDeleteClub?.let { club ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    db.collection("clubs").document(club.id).delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Club deleted", Toast.LENGTH_SHORT).show()
                                            selectedDeleteClub = null
                                        }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Confirm Delete", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddOrEditClubForm(
    club: Club? = null,
    onSubmit: (Club) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(TextFieldValue(club?.name ?: "")) }
    var coach by remember { mutableStateOf(TextFieldValue(club?.coach ?: "")) }
    var contact by remember { mutableStateOf(TextFieldValue(club?.contact ?: "")) }
    var description by remember { mutableStateOf(TextFieldValue(club?.description ?: "")) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf(club?.imageUrl ?: "") }

    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    @Composable
    fun buildField(label: String, value: TextFieldValue, onChange: (TextFieldValue) -> Unit, singleLine: Boolean = true) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            buildField(label = "Name", value = name, onChange = { name = it })
            buildField(label = "Representative", value = coach, onChange = { coach = it })
            buildField(label = "Cell Number", value = contact, onChange = { contact = it })

            buildField("Description", description, onChange = { description = it }, singleLine = false)

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { launcher.launch("image/*") }) {
                Text(if (imageUri != null || imageUrl.isNotBlank()) "Logo Selected" else "Select Logo")
            }

            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            imageUrl.takeIf { it.isNotBlank() && imageUri == null }?.let {
                Image(
                    painter = rememberAsyncImagePainter(Uri.parse(it)),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                enabled = !isUploading,
                onClick = {
                    if (name.text.isBlank() || coach.text.isBlank() || contact.text.isBlank()) {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        isUploading = true
                        val savedImagePath = imageUri?.let {
                            saveImageToInternalStorage(context, it)
                        }

                        val finalImageUrl = savedImagePath ?: imageUrl
                        val clubId = club?.id ?: UUID.randomUUID().toString()

                        onSubmit(
                            Club(
                                id = clubId,
                                name = name.text,
                                coach = coach.text,
                                contact = contact.text,
                                description = description.text,
                                imageUrl = finalImageUrl
                            )
                        )
                        isUploading = false
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (club == null) "Submit" else "Update")
            }
        }
    }
}

suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String {
    return withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "club_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        file.toURI().toString() // Returns file:// path
    }
}

@Composable
fun ClubListItem(club: Club, highlight: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) Color.DarkGray else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            club.imageUrl.takeIf { it.isNotBlank() }?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
            }

            Text(
                text = club.name,
                fontSize = 15.sp,
                color = if (highlight) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BoxItem(label: String, onClick: () -> Unit) {
    Surface(
        tonalElevation = 2.dp, shadowElevation = 4.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .background(colorScheme.secondaryContainer)
                .padding(vertical = 16.dp, horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = label,
                fontSize = 17.sp,
                color = colorScheme.onSecondaryContainer
            )
        }
    }
}
