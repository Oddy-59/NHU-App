package com.example.nhu_app.admin.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.firestore.ListenerRegistration


data class Team(
    val id: String = "",
    val teamName: String = "",
    val club: String = "",
    val representative: String = "",
    val cellNumber: String = "",
    val email: String = "",
    val leagues: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamReg(navController: NavHostController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var teams by remember { mutableStateOf<List<Team>>(emptyList()) }
    var showAddForm by remember { mutableStateOf(false) }
    var showViewTeams by remember { mutableStateOf(false) }
    var showRemoveTeam by remember { mutableStateOf(false) }

    var selectedEditTeam by remember { mutableStateOf<Team?>(null) }
    var selectedDeleteTeam by remember { mutableStateOf<Team?>(null) }

    var listenerRegistration: ListenerRegistration? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        listenerRegistration = db.collection("teams")
            .addSnapshotListener { snapshot, error ->
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

        onDispose { listenerRegistration?.remove() }
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Team Management:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }

            item {
                BoxItemTR("Add Team") { showAddForm = !showAddForm }
            }

            if (showAddForm) {
                item {
                    AddOrEditTeamForm(onSubmit = { newTeam ->
                        val duplicate = teams.any {
                            it.teamName.equals(newTeam.teamName, ignoreCase = true) &&
                                    it.club.equals(newTeam.club, ignoreCase = true)
                        }

                        if (duplicate) {
                            Toast.makeText(context, "Team already exists.", Toast.LENGTH_SHORT).show()
                            return@AddOrEditTeamForm
                        }

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
                    })
                }
            }

            item {
                BoxItemTR("View Registered Teams") { showViewTeams = !showViewTeams }
            }

            if (showViewTeams) {
                item {
                    TeamList(
                        teams = teams,
                        onClick = { team ->
                            selectedEditTeam = if (selectedEditTeam?.id == team.id) null else team
                        }
                    )
                }

                selectedEditTeam?.let { team ->
                    item {
                        AddOrEditTeamForm(team = team, onSubmit = { updated ->
                            val teamData = hashMapOf(
                                "teamName" to updated.teamName,
                                "club" to updated.club,
                                "representative" to updated.representative,
                                "cellNumber" to updated.cellNumber,
                                "email" to updated.email,
                                "leagues" to updated.leagues
                            )
                            db.collection("teams").document(team.id)
                                .update(teamData as Map<String, Any>)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Team updated", Toast.LENGTH_SHORT).show()
                                    selectedEditTeam = null
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                                }
                        })
                    }
                }
            }

            item {
                BoxItemTR("Delete Team(s)") { showRemoveTeam = !showRemoveTeam }
            }

            if (showRemoveTeam) {
                item {
                    TeamList(
                        teams = teams,
                        selectedTeam = selectedDeleteTeam,
                        onClick = { team ->
                            selectedDeleteTeam = if (selectedDeleteTeam?.id == team.id) null else team
                        }
                    )
                }

                selectedDeleteTeam?.let { team ->
                    item {
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
                                Text("Delete", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddOrEditTeamForm(team: Team? = null, onSubmit: (Team) -> Unit) {
    val context = LocalContext.current

    var teamName by remember { mutableStateOf(TextFieldValue(team?.teamName ?: "")) }
    var club by remember { mutableStateOf(TextFieldValue(team?.club ?: "")) }
    var representative by remember { mutableStateOf(TextFieldValue(team?.representative ?: "")) }
    var cellNumber by remember { mutableStateOf(TextFieldValue(team?.cellNumber ?: "")) }
    var email by remember { mutableStateOf(TextFieldValue(team?.email ?: "")) }
    var leagues by remember { mutableStateOf(TextFieldValue(team?.leagues ?: "")) }

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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            buildField("Team Name", teamName) { teamName = it }
            buildField("Club", club) { club = it }
            buildField("Representative", representative) { representative = it }
            buildField("Cell Number", cellNumber) { cellNumber = it }
            buildField("Email", email) { email = it }
            OutlinedTextField(
                value = leagues,
                onValueChange = { leagues = it },
                label = { Text("Leagues") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                singleLine = false
            )

            Button(
                onClick = {
                    if (teamName.text.isBlank() || club.text.isBlank() || representative.text.isBlank() ||
                        cellNumber.text.isBlank() || email.text.isBlank() || leagues.text.isBlank()
                    ) {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    onSubmit(
                        Team(
                            id = team?.id ?: "",
                            teamName = teamName.text,
                            club = club.text,
                            representative = representative.text,
                            cellNumber = cellNumber.text,
                            email = email.text,
                            leagues = leagues.text
                        )
                    )
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (team == null) "Submit" else "Update")
            }
        }
    }
}

@Composable
fun TeamList(
    teams: List<Team>,
    selectedTeam: Team? = null,
    onClick: (Team) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        teams.forEach { team ->
            val isSelected = selectedTeam?.id == team.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(team) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        Color.Gray
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(modifier = Modifier.padding(15.dp)) {
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

@Composable
fun BoxItemTR(label: String, onClick: () -> Unit) {
    Surface(
        tonalElevation = 2.dp, shadowElevation = 4.dp,
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

@Preview(showBackground = true)
@Composable
fun TeamRegPreview() {
    val navController = rememberNavController()
    TeamReg(navController)
}
