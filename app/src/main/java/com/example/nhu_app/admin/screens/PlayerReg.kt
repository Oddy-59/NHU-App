package com.example.nhu_app.admin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.nhu_app.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

// ✅ Player data class with Firestore constructor support
data class Player(
    val name: String = "",
    val age: String = "",
    val gender: String = "",
    val position: String = "",
    val team: String = "",
    val club: String = ""
)

@Composable
fun PlayerReg(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val players = remember { mutableStateListOf<Player>() }

    var showAddForm by remember { mutableStateOf(false) }
    var showEditList by remember { mutableStateOf(false) }
    var showViewPlayers by remember { mutableStateOf(false) }
    var showRemovePlayer by remember { mutableStateOf(false) }

    var selectedEditPlayer by remember { mutableStateOf<Player?>(null) }
    var selectedDeletePlayer by remember { mutableStateOf<Player?>(null) }

    // ✅ Firestore uses unique IDs; cannot use player.name directly as document ID unless you ensure uniqueness
    suspend fun fetchPlayers() {
        try {
            val result = db.collection("players").get().await()
            players.clear()
            for (document in result) {
                val player = document.toObject<Player>()
                players.add(player)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(Unit) {
        fetchPlayers()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        BoxItemPR("Add Player") { showAddForm = !showAddForm }
        if (showAddForm) AddPlayerForm { player ->
            db.collection("players").add(player).addOnSuccessListener {
                players.add(player)
                showAddForm = false
            }
        }

        BoxItemPR("Edit Player Details") { showEditList = !showEditList }
        if (showEditList) {
            PlayerList(players = players, onClick = { selectedEditPlayer = it })
            selectedEditPlayer?.let { playerToEdit ->
                EditPlayerForm(playerToEdit) { updatedPlayer ->
                    // 🔁 Must search the actual document to get ID
                    db.collection("players")
                        .whereEqualTo("name", playerToEdit.name)
                        .get()
                        .addOnSuccessListener { result ->
                            for (doc in result) {
                                db.collection("players").document(doc.id).set(updatedPlayer)
                                players[players.indexOf(playerToEdit)] = updatedPlayer
                                selectedEditPlayer = null
                                break
                            }
                        }
                }
            }
        }

        BoxItemPR("View Registered Players") { showViewPlayers = !showViewPlayers }
        if (showViewPlayers) PlayerList(players = players, onClick = {})

        BoxItemPR("Remove Player") { showRemovePlayer = !showRemovePlayer }
        if (showRemovePlayer) {
            PlayerList(players = players, onClick = { selectedDeletePlayer = it })
            selectedDeletePlayer?.let { playerToDelete ->
                Button(
                    onClick = {
                        db.collection("players")
                            .whereEqualTo("name", playerToDelete.name)
                            .get()
                            .addOnSuccessListener { result ->
                                for (doc in result) {
                                    db.collection("players").document(doc.id).delete()
                                    players.remove(playerToDelete)
                                    selectedDeletePlayer = null
                                    break
                                }
                            }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.End)
                ) {
                    Text("Delete Player", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun BoxItemPR(label: String, onClick: () -> Unit) {
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
            Text(text = label, fontSize = 16.sp, color = Color.Black)
        }
    }
}

@Composable
fun AddPlayerForm(onAdd: (Player) -> Unit) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var age by remember { mutableStateOf(TextFieldValue()) }
    var gender by remember { mutableStateOf(TextFieldValue()) }
    var position by remember { mutableStateOf(TextFieldValue()) }
    var team by remember { mutableStateOf(TextFieldValue()) }
    var club by remember { mutableStateOf(TextFieldValue()) }
    var showError by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        listOf(
            "Name" to name, "Age" to age, "Gender" to gender,
            "Position" to position, "Team" to team, "Club" to club
        ).forEach { (label, value) ->
            OutlinedTextField(
                value = value,
                onValueChange = {
                    showError = false
                    when (label) {
                        "Name" -> name = it
                        "Age" -> age = it
                        "Gender" -> gender = it
                        "Position" -> position = it
                        "Team" -> team = it
                        "Club" -> club = it
                    }
                },
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showError) {
            Text("All fields are required.", color = Color.Red, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (name.text.isBlank() || age.text.isBlank() || gender.text.isBlank()
                    || position.text.isBlank() || team.text.isBlank() || club.text.isBlank()
                ) {
                    showError = true
                } else {
                    onAdd(Player(name.text, age.text, gender.text, position.text, team.text, club.text))
                    // Clear fields
                    name = TextFieldValue()
                    age = TextFieldValue()
                    gender = TextFieldValue()
                    position = TextFieldValue()
                    team = TextFieldValue()
                    club = TextFieldValue()
                    showError = false
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Player")
        }
    }
}

@Composable
fun EditPlayerForm(player: Player, onUpdate: (Player) -> Unit) {
    var name by remember { mutableStateOf(TextFieldValue(player.name)) }
    var age by remember { mutableStateOf(TextFieldValue(player.age)) }
    var gender by remember { mutableStateOf(TextFieldValue(player.gender)) }
    var position by remember { mutableStateOf(TextFieldValue(player.position)) }
    var team by remember { mutableStateOf(TextFieldValue(player.team)) }
    var club by remember { mutableStateOf(TextFieldValue(player.club)) }
    var showError by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        listOf(
            "Name" to name, "Age" to age, "Gender" to gender,
            "Position" to position, "Team" to team, "Club" to club
        ).forEach { (label, value) ->
            OutlinedTextField(
                value = value,
                onValueChange = {
                    showError = false
                    when (label) {
                        "Name" -> name = it
                        "Age" -> age = it
                        "Gender" -> gender = it
                        "Position" -> position = it
                        "Team" -> team = it
                        "Club" -> club = it
                    }
                },
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showError) {
            Text("All fields are required.", color = Color.Red, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (name.text.isBlank() || age.text.isBlank() || gender.text.isBlank()
                    || position.text.isBlank() || team.text.isBlank() || club.text.isBlank()
                ) {
                    showError = true
                } else {
                    onUpdate(Player(name.text, age.text, gender.text, position.text, team.text, club.text))
                    showError = false
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
fun PlayerList(players: List<Player>, onClick: (Player) -> Unit) {
    Column {
        players.forEach { player ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onClick(player) }
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(player.name, fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerReg() {
    val navController = rememberNavController()
    PlayerReg(navController = navController)
}