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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

data class Player(
    val id: String = "",
    val name: String = "",
    val age: String = "",
    val gender: String = "",
    val position: String = "",
    val team: String = "",
    val club: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerReg(navController: NavHostController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var players by remember { mutableStateOf<List<Player>>(emptyList()) }
    var showAddForm by remember { mutableStateOf(false) }
    var showViewPlayers by remember { mutableStateOf(false) }
    var showRemovePlayer by remember { mutableStateOf(false) }
    var selectedDeletePlayer by remember { mutableStateOf<Player?>(null) }
    var selectedEditPlayer by remember { mutableStateOf<Player?>(null) }

    var listenerRegistration: ListenerRegistration? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        listenerRegistration = db.collection("players")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(context, "Failed to load players.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                players = snapshot?.documents?.map { doc ->
                    Player(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        age = doc.getString("age") ?: "",
                        gender = doc.getString("gender") ?: "",
                        position = doc.getString("position") ?: "",
                        team = doc.getString("team") ?: "",
                        club = doc.getString("club") ?: ""
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
                    text = "Player Management:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }

            item {
                BoxItemPR("Add Player") { showAddForm = !showAddForm }
            }

            if (showAddForm) {
                item {
                    AddOrEditPlayerForm(onSubmit = { newPlayer ->
                        val duplicate = players.any { it.name == newPlayer.name && it.team == newPlayer.team }
                        if (duplicate) {
                            Toast.makeText(context, "Player already exists.", Toast.LENGTH_SHORT).show()
                            return@AddOrEditPlayerForm
                        }

                        val playerData = hashMapOf(
                            "name" to newPlayer.name,
                            "age" to newPlayer.age,
                            "gender" to newPlayer.gender,
                            "position" to newPlayer.position,
                            "team" to newPlayer.team,
                            "club" to newPlayer.club
                        )

                        db.collection("players")
                            .add(playerData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Player added successfully", Toast.LENGTH_SHORT).show()
                                showAddForm = false
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to add player.", Toast.LENGTH_SHORT).show()
                            }
                    })
                }
            }

            item {
                BoxItemPR("View Registered Players") { showViewPlayers = !showViewPlayers }
            }

            if (showViewPlayers) {
                items(players) { player ->
                    PlayerList(players = listOf(player)) {
                        selectedEditPlayer = if (selectedEditPlayer?.id == it.id) null else it
                    }
                }

                selectedEditPlayer?.let { player ->
                    item {
                        AddOrEditPlayerForm(player = player, onSubmit = { updated ->
                            val playerData = mapOf(
                                "name" to updated.name,
                                "age" to updated.age,
                                "gender" to updated.gender,
                                "position" to updated.position,
                                "team" to updated.team,
                                "club" to updated.club
                            )
                            db.collection("players").document(player.id)
                                .update(playerData)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Player updated", Toast.LENGTH_SHORT).show()
                                    selectedEditPlayer = null
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                                }
                        })
                    }
                }
            }

            item {
                BoxItemPR("Delete Player(s)") { showRemovePlayer = !showRemovePlayer }
            }

            if (showRemovePlayer) {
                items(players) { player ->
                    PlayerList(players = listOf(player), selectedPlayer = selectedDeletePlayer) { clickedPlayer ->
                        selectedDeletePlayer = if (selectedDeletePlayer?.id == clickedPlayer.id) null else clickedPlayer
                    }
                }

                selectedDeletePlayer?.let { player ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    db.collection("players").document(player.id).delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Player deleted", Toast.LENGTH_SHORT).show()
                                            selectedDeletePlayer = null
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Error deleting player.", Toast.LENGTH_SHORT).show()
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
fun AddOrEditPlayerForm(player: Player? = null, onSubmit: (Player) -> Unit) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(TextFieldValue(player?.name ?: "")) }
    var age by remember { mutableStateOf(TextFieldValue(player?.age ?: "")) }
    var gender by remember { mutableStateOf(TextFieldValue(player?.gender ?: "")) }
    var position by remember { mutableStateOf(TextFieldValue(player?.position ?: "")) }
    var team by remember { mutableStateOf(TextFieldValue(player?.team ?: "")) }
    var club by remember { mutableStateOf(TextFieldValue(player?.club ?: "")) }

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
            buildField("Name", name) { name = it }
            buildField("Age", age) { age = it }
            buildField("Gender", gender) { gender = it }
            buildField("Position", position) { position = it }
            buildField("Team", team) { team = it }
            buildField("Club", club) { club = it }

            Button(
                onClick = {
                    if (name.text.isBlank() || age.text.isBlank() || gender.text.isBlank() ||
                        position.text.isBlank() || team.text.isBlank() || club.text.isBlank()
                    ) {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    onSubmit(
                        Player(
                            id = player?.id ?: "",
                            name = name.text,
                            age = age.text,
                            gender = gender.text,
                            position = position.text,
                            team = team.text,
                            club = club.text
                        )
                    )
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (player == null) "Submit" else "Update")
            }
        }
    }
}

@Composable
fun BoxItemPR(label: String, onClick: () -> Unit) {
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
fun PlayerList(
    players: List<Player>,
    selectedPlayer: Player? = null,
    onClick: (Player) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        players.forEach { player ->
            val isSelected = selectedPlayer?.id == player.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(player) },
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
                        text = "${player.name} - ${player.team}",
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
fun PlayerRegPreview() {
    val navController = rememberNavController()
    PlayerReg(navController)
}