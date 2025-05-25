package com.example.nhu_app.admin.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

data class Event(
    val id: String = "",
    val title: String = "",
    val venue: String = "",
    val date: String = "",
    val time: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Entries(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val events = remember { mutableStateListOf<Event>() }
    var showAdd by remember { mutableStateOf(false) }
    var showManage by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    // Load Events
    LaunchedEffect(Unit) {
        db.collection("events")
            .get()
            .addOnSuccessListener { result ->
                events.clear()
                for (document in result) {
                    val event = Event(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        venue = document.getString("venue") ?: "",
                        date = document.getString("date") ?: "",
                        time = document.getString("time") ?: ""
                    )
                    events.add(event)
                }
            }
            .addOnFailureListener {
                Log.e("Entries", "Failed to load events: ${it.message}")
            }
    }

    // UI Layout
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
                    text = "Event Management:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }

            // Add Event Section
            item {
                EntryOptionBox("Add Event") { showAdd = !showAdd }
            }

            if (showAdd) {
                item {
                    EventForm(onSubmit = { newEvent ->
                        db.collection("events")
                            .add(
                                mapOf(
                                    "title" to newEvent.title,
                                    "venue" to newEvent.venue,
                                    "date" to newEvent.date,
                                    "time" to newEvent.time
                                )
                            )
                            .addOnSuccessListener { docRef ->
                                events.add(newEvent.copy(id = docRef.id))
                                showAdd = false
                            }
                    })
                }
            }

            // Manage Event Section
            item {
                EntryOptionBox("Reschedule/Cancel Event") { showManage = !showManage }
            }

            if (showManage) {
                if (selectedEvent == null) {
                    items(events) { event ->
                        EventList(events = listOf(event)) {
                            selectedEvent = it
                        }
                    }
                } else {
                    item {
                        selectedEvent?.let { event ->
                            EventForm(
                                event = event,
                                onSubmit = { updatedEvent ->
                                    db.collection("events").document(event.id)
                                        .set(
                                            mapOf(
                                                "title" to updatedEvent.title,
                                                "venue" to updatedEvent.venue,
                                                "date" to updatedEvent.date,
                                                "time" to updatedEvent.time
                                            )
                                        )
                                        .addOnSuccessListener {
                                            val index = events.indexOfFirst { it.id == event.id }
                                            if (index != -1) {
                                                events[index] = updatedEvent.copy(id = event.id)
                                            }
                                            selectedEvent = null
                                        }
                                },
                                onCancel = {
                                    db.collection("events").document(event.id)
                                        .delete()
                                        .addOnSuccessListener {
                                            events.removeIf { it.id == event.id }
                                            selectedEvent = null
                                        }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun EntryOptionBox(label: String, onClick: () -> Unit) {
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

@Composable
fun EventForm(
    event: Event? = null,
    onSubmit: (Event) -> Unit,
    onCancel: (() -> Unit)? = null
) {
    var title by remember { mutableStateOf(TextFieldValue(event?.title ?: "")) }
    var venue by remember { mutableStateOf(TextFieldValue(event?.venue ?: "")) }
    var date by remember { mutableStateOf(event?.date ?: "") }
    var time by remember { mutableStateOf(event?.time ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = venue,
                onValueChange = { venue = it },
                label = { Text("Venue") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        date = "$day/${month + 1}/$year"
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text(if (date.isEmpty()) "Select Date" else "Date: $date")
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        time = String.format("%02d:%02d", hour, minute)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }) {
                Text(if (time.isEmpty()) "Select Time" else "Time: $time")
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                onCancel?.let {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onError)
                    }
                    Spacer(Modifier.width(8.dp))
                }

                Button(onClick = {
                    if (title.text.isBlank() || venue.text.isBlank() || date.isBlank() || time.isBlank()) {
                        errorMessage = "Please fill in all fields"
                        return@Button
                    }

                    onSubmit(
                        Event(
                            id = event?.id ?: "",
                            title = title.text,
                            venue = venue.text,
                            date = date,
                            time = time
                        )
                    )

                    if (event == null) {
                        title = TextFieldValue()
                        venue = TextFieldValue()
                        date = ""
                        time = ""
                    }

                    errorMessage = ""
                }) {
                    Text(if (event == null) "Add Event" else "Save Changes")
                }
            }
        }
    }
}

@Composable
fun EventList(events: List<Event>, onClick: (Event) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        events.forEach { event ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(event) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(event.title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${event.date} at ${event.time} — ${event.venue}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEntries() {
    val navController = rememberNavController()
    Entries(navController = navController)
}