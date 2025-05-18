package com.example.nhu_app.admin.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme


data class Team(
    val id: String = "",
    val teamName: String = "",
    val club: String = "",
    val representative: String = "",
    val cellNumber: String = "",
    val email: String = "",
    val leagues: String = ""
)

@Composable
fun TeamReg(navController: NavHostController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var teams by remember { mutableStateOf<List<Team>>(emptyList()) }
    var showAddForm by remember { mutableStateOf(false) }
    var showViewTeams by remember { mutableStateOf(false) }
    var showRemoveTeam by remember { mutableStateOf(false) }
    var selectedDeleteTeam by remember { mutableStateOf<Team?>(null) }

    LaunchedEffect(Unit) {
        db.collection("teams").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(context, "Failed to load teams.", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            teams = snapshot?.documents?.map { doc ->
                Team(
                    id = doc.id,
                    teamName = doc.getString("teamName") ?: "",
                    club = doc.getString("club") ?: "",
                    representative = doc.getString("representative") ?: "",
                    cellNumber = doc.getString("cellNumber") ?: "",
                    email = doc.getString("email") ?: "",
                    leagues = doc.getString("leagues") ?: ""
                )
            } ?: emptyList()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            BoxItemTR("➕ Add Team") { showAddForm = !showAddForm }
            if (showAddForm) {
                Spacer(modifier = Modifier.height(12.dp))
                AddTeamForm { newTeam ->
                    val teamData = hashMapOf(
                        "teamName" to newTeam.teamName,
                        "club" to newTeam.club,
                        "representative" to newTeam.representative,
                        "cellNumber" to newTeam.cellNumber,
                        "email" to newTeam.email,
                        "leagues" to newTeam.leagues
                    )
                    db.collection("teams")
                        .add(teamData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Team added successfully", Toast.LENGTH_SHORT).show()
                            showAddForm = false
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to add team.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        item {
            BoxItemTR("📋 View Registered Teams") { showViewTeams = !showViewTeams }
            if (showViewTeams) {
                Spacer(modifier = Modifier.height(12.dp))
                TeamList(teams = teams, onClick = {})
            }
        }

        item {
            BoxItemTR("🗑 Delete Team(s)") { showRemoveTeam = !showRemoveTeam }
            if (showRemoveTeam) {
                Spacer(modifier = Modifier.height(12.dp))
                TeamList(teams = teams, onClick = { selectedDeleteTeam = it })

                selectedDeleteTeam?.let { team ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                db.collection("teams").document(team.id).delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Team deleted", Toast.LENGTH_SHORT).show()
                                        selectedDeleteTeam = null
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Error deleting team.", Toast.LENGTH_SHORT).show()
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

@Composable
fun BoxItemTR(label: String, onClick: () -> Unit) {
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
fun AddTeamForm(onAdd: (Team) -> Unit) {
    val context = LocalContext.current

    var teamName by remember { mutableStateOf(TextFieldValue()) }
    var clubName by remember { mutableStateOf(TextFieldValue()) }
    var contactPerson by remember { mutableStateOf(TextFieldValue()) }
    var contactNumber by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf(TextFieldValue()) }
    var leaguesInterested by remember { mutableStateOf(TextFieldValue()) }

    @Composable
    fun buildField(label: String, value: TextFieldValue, onChange: (TextFieldValue) -> Unit) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            buildField("Team Name", teamName) { teamName = it }
            buildField("Club", clubName) { clubName = it }
            buildField("Representative", contactPerson) { contactPerson = it }
            buildField("Cell Number", contactNumber) { contactNumber = it }
            buildField("Email", email) { email = it }

            OutlinedTextField(
                value = leaguesInterested,
                onValueChange = { leaguesInterested = it },
                label = { Text("League(s) Interested") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4
            )

            Button(
                onClick = onClick@{
                    if (teamName.text.isBlank() || clubName.text.isBlank() ||
                        contactPerson.text.isBlank() || contactNumber.text.isBlank() ||
                        email.text.isBlank() || leaguesInterested.text.isBlank()
                    ) {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        return@onClick
                    }

                    onAdd(
                        Team(
                            teamName = teamName.text,
                            club = clubName.text,
                            representative = contactPerson.text,
                            cellNumber = contactNumber.text,
                            email = email.text,
                            leagues = leaguesInterested.text
                        )
                    )

                    Toast.makeText(context, "Team submitted", Toast.LENGTH_SHORT).show()

                    teamName = TextFieldValue()
                    clubName = TextFieldValue()
                    contactPerson = TextFieldValue()
                    contactNumber = TextFieldValue()
                    email = TextFieldValue()
                    leaguesInterested = TextFieldValue()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Submit")
            }
        }
    }
}


@Composable
fun TeamList(teams: List<Team>, onClick: (Team) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        teams.forEach { team ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(team) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(15.dp)
                ) {
                    Text(
                        text = "${team.teamName} - ${team.club}",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TeamRegPreview() {
    val navController = rememberNavController()
    TeamReg(navController)
}
