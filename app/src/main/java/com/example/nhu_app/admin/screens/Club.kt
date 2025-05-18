package com.example.nhu_app.admin.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

data class Club(
    val id: String = "",
    val name: String = "",
    val coach: String = "",
    val description: String = "",
    val contact: String = "",
    val imageUrl: String = ""
)

@Composable
fun Club(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val clubList = remember { mutableStateListOf<Club>() }

    var showAddForm by remember { mutableStateOf(false) }
    var showEditForm by remember { mutableStateOf(false) }
    var showDeleteList by remember { mutableStateOf(false) }

    var selectedEditClub by remember { mutableStateOf<Club?>(null) }
    var selectedDeleteClub by remember { mutableStateOf<Club?>(null) }

    // Load clubs initially
    LaunchedEffect(Unit) {
        db.collection("clubs").get()
            .addOnSuccessListener { result ->
                clubList.clear()
                for (doc in result) {
                    doc.toObject(Club::class.java).let { clubList.add(it) }
                }
            }
    }

    Column(Modifier.padding(16.dp)) {
        BoxItem("Add Club") { showAddForm = !showAddForm }
        if (showAddForm) {
            ClubForm(onSubmit = { newClub ->
                db.collection("clubs").document(newClub.id).set(newClub)
                    .addOnSuccessListener {
                        clubList.add(newClub)
                        showAddForm = false
                    }
            })
        }

        BoxItem("Edit Club") { showEditForm = !showEditForm }
        if (showEditForm) {
            ClubList(clubList) { club -> selectedEditClub = club }
            selectedEditClub?.let { it ->
                ClubForm(club = it, onSubmit = { updated ->
                    db.collection("clubs").document(updated.id).set(updated)
                        .addOnSuccessListener {
                            val index = clubList.indexOfFirst { it.id == updated.id }
                            if (index != -1) {
                                clubList[index] = updated
                            }
                            selectedEditClub = null
                        }
                })
            }
        }

        BoxItem("Delete Club") { showDeleteList = !showDeleteList }
        if (showDeleteList) {
            ClubList(clubList) { club -> selectedDeleteClub = club }
            selectedDeleteClub?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            selectedDeleteClub?.let { club ->
                                db.collection("clubs").document(club.id).delete()
                                    .addOnSuccessListener {
                                        clubList.remove(club)
                                        selectedDeleteClub = null
                                    }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete Club", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ClubForm(club: Club? = null, onSubmit: (Club) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf(TextFieldValue(club?.name ?: "")) }
    var coach by remember { mutableStateOf(TextFieldValue(club?.coach ?: "")) }
    var desc by remember { mutableStateOf(TextFieldValue(club?.description ?: "")) }
    var contact by remember { mutableStateOf(TextFieldValue(club?.contact ?: "")) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf(club?.imageUrl ?: "") }
    var isUploading by remember { mutableStateOf(false) }

    // Validation error states
    var nameError by remember { mutableStateOf(false) }
    var coachError by remember { mutableStateOf(false) }
    var contactError by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    Column(Modifier.padding(vertical = 8.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = false
            },
            label = { Text("Name") },
            isError = nameError,
            modifier = Modifier.fillMaxWidth()
        )
        if (nameError) {
            Text("Name is required", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        OutlinedTextField(
            value = coach,
            onValueChange = {
                coach = it
                coachError = false
            },
            label = { Text("Representative") },
            isError = coachError,
            modifier = Modifier.fillMaxWidth()
        )
        if (coachError) {
            Text("Representative is required", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        OutlinedTextField(
            value = contact,
            onValueChange = {
                contact = it
                contactError = false
            },
            label = { Text("Cell Number") },
            isError = contactError,
            modifier = Modifier.fillMaxWidth()
        )
        if (contactError) {
            Text("Cell number is required", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Club Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text(if (imageUri != null || imageUrl.isNotBlank()) "Logo Selected" else "Select Logo")
        }

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(top = 8.dp),
                contentScale = ContentScale.Crop
            )
        }

        imageUrl.takeIf { it.isNotBlank() && imageUri == null }?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(top = 8.dp),
                contentScale = ContentScale.Crop
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                enabled = !isUploading,
                onClick = {
                    // Validate inputs
                    val isValid = name.text.isNotBlank().also { nameError = !it } &&
                            coach.text.isNotBlank().also { coachError = !it } &&
                            contact.text.isNotBlank().also { contactError = !it }

                    if (!isValid) return@Button

                    isUploading = true
                    val id = club?.id ?: UUID.randomUUID().toString()

                    val updated = Club(
                        id = id,
                        name = name.text,
                        coach = coach.text,
                        description = desc.text,
                        contact = contact.text,
                        imageUrl = imageUri?.toString() ?: imageUrl
                    )

                    onSubmit(updated)
                    isUploading = false
                }
            ) {
                Text(if (club == null) "Add Club" else "Save Changes")
            }
        }
    }
}

@Composable
fun ClubList(clubs: List<Club>, onClick: (Club) -> Unit) {
    Column {
        clubs.forEach { club ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onClick(club) }
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                club.imageUrl.takeIf { it.isNotBlank() }?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(club.name, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun BoxItem(label: String, onClick: () -> Unit) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFE0E0E0))
                .height(60.dp)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(label, fontSize = 16.sp, color = Color.Black)
        }
    }
}
