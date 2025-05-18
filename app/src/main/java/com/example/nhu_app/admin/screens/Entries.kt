package com.example.nhu_app.admin.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun Entries(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val events = remember { mutableStateListOf<Event>() }
    var showAdd by remember { mutableStateOf(false) }
    var showManage by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    // Load events on first render
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

    Column(Modifier.padding(16.dp)) {
        EntryOptionBox("Add Event") { showAdd = !showAdd }
        if (showAdd) {
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

        EntryOptionBox("Reschedule/Cancel Event") { showManage = !showManage }
        if (showManage) {
            // Show only the list of events first
            if (selectedEvent == null) {
                EventList(events) { event ->
                    selectedEvent = event
                }
            } else {
                // Once an event is selected, show the modify/cancel options
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

@Composable
fun EntryOptionBox(label: String, onClick: () -> Unit) {
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
                .padding(start = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = label, fontSize = 16.sp, color = Color.Black)
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

    Column(modifier = Modifier.padding(top = 12.dp)) {
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
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

        Column {
            Button(onClick = {
                val dpd = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        date = "$dayOfMonth/${month + 1}/$year"
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                dpd.show()
            }) {
                Text(text = if (date.isEmpty()) "Select Date" else "Date: $date")
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                val tpd = TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        time = String.format("%02d:%02d", hour, minute)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                tpd.show()
            }) {
                Text(text = if (time.isEmpty()) "Select Time" else "Time: $time")
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            onCancel?.let {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cancel", color = Color.White)
                }
                Spacer(Modifier.width(8.dp))
            }

            Button(onClick = {
                if (title.text.isBlank() || venue.text.isBlank() || date.isBlank() || time.isBlank()) {
                    errorMessage = "Please fill in all fields"
                    return@Button
                }

                val result = Event(
                    id = event?.id ?: "",
                    title = title.text,
                    venue = venue.text,
                    date = date,
                    time = time
                )
                onSubmit(result)

                // Clear fields if adding a new event
                if (event == null) {
                    title = TextFieldValue("")
                    venue = TextFieldValue("")
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

@Composable
fun EventList(events: List<Event>, onClick: (Event) -> Unit) {
    Column {
        events.forEach { event ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onClick(event) }
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp)
            ) {
                Column {
                    Text(event.title, fontSize = 16.sp, color = Color.Black)
                    Text("${event.date} at ${event.time} — ${event.venue}", fontSize = 14.sp, color = Color.Gray)
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