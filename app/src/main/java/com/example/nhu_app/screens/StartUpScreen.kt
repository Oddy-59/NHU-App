package com.example.nhu_app.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.nhu_app.R

@Composable
fun StartUpScreen(navController: NavController) {

    val context = LocalContext.current

    var userInput by remember { mutableStateOf("") }
    var adminInput by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var adminPassword by remember { mutableStateOf("") }

    var showUserPassword by remember { mutableStateOf(false) }
    var showAdminPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Welcome To The NHU App",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 12.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Icon(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            tint = Color.Black,
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login as:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (adminInput.isBlank()) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = {
                            userInput = it
                            showUserPassword = true
                            showAdminPassword = false
                            adminInput = ""
                        },
                        label = { Text("User", color = Color.Black) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_user),
                                contentDescription = "User Icon",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF015A80)
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )
                }

                if (showUserPassword) {
                    OutlinedTextField(
                        value = userPassword,
                        onValueChange = { userPassword = it },
                        label = { Text("Password", color = Color.Black) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_password),
                                contentDescription = "Password Icon",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF015A80)
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (userInput.isNotBlank() && userPassword.length >= 6) {
                                    navController.navigate("home")
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )

                    if (userPassword.isNotEmpty() && userPassword.length < 6) {
                        Text(
                            text = "Password must be at least 6 characters",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 4.dp, bottom = 4.dp)
                        )
                    }
                }

                if (userInput.isBlank()) {
                    OutlinedTextField(
                        value = adminInput,
                        onValueChange = {
                            adminInput = it
                            showAdminPassword = true
                            showUserPassword = false
                            userInput = ""
                        },
                        label = { Text("Admin", color = Color.Black) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_admin),
                                contentDescription = "Admin Icon",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF015A80)
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )
                }

                if (showAdminPassword) {
                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = { adminPassword = it },
                        label = { Text("Password", color = Color.Black) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_password),
                                contentDescription = "Password Icon",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF015A80)
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (adminInput.isNotBlank() && adminPassword.length >= 6) {
                                    navController.navigate("admin")
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )

                    if (adminPassword.isNotEmpty() && adminPassword.length < 6) {
                        Text(
                            text = "Password must be at least 6 characters",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 4.dp, bottom = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (userInput.isNotBlank() && userPassword.length >= 6) {
                            navController.navigate("home")
                        } else if (adminInput.isNotBlank() && adminPassword.length >= 6) {
                            navController.navigate("admin")
                        }
                        else {
                            Toast.makeText(context, "Please enter the required fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF015A80))
                ) {
                    Text("Login", fontSize = 16.sp, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Don't have an account? Sign up",
            color = Color.Blue,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                navController.navigate("signup")
            }
        )
    }
}


@Preview(showBackground = true, name = "StartUp Screen")
@Composable
fun PreviewStartUpScreen() {
    val navController = rememberNavController()
    StartUpScreen(navController = navController)
}