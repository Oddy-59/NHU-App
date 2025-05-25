package com.example.nhu_app.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nhu_app.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme

    fun isValidEmail(input: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }

    fun isValidPassword(pw: String): Boolean {
        return pw.length in 6..10 && pw.any { !it.isLetterOrDigit() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .background(colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Welcome Admin!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground,
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 12.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(30.dp))

                Icon(
                    painter = painterResource(id = R.drawable.logo1),
                    contentDescription = "App Logo",
                    tint = colorScheme.primary,
                    modifier = Modifier
                        .size(140.dp)
                        .padding(bottom = 24.dp)
                )

                Spacer(modifier = Modifier.height(50.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Login",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = {
                                emailInput = it
                                emailError = false
                            },
                            label = { Text("Email", color = colorScheme.onSurface) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_email),
                                    contentDescription = "Email Icon",
                                    modifier = Modifier.size(20.dp),
                                    tint = colorScheme.primary
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            textStyle = LocalTextStyle.current.copy(color = colorScheme.onSurface)
                        )

                        if (emailError) {
                            Text(
                                text = "Please enter a valid email address.",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(start = 4.dp, bottom = 4.dp)
                            )
                        }

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = {
                                if (it.length <= 10) passwordInput = it
                            },
                            label = { Text("Password", color = colorScheme.onSurface) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_password),
                                    contentDescription = "Password Icon",
                                    modifier = Modifier.size(20.dp),
                                    tint = colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                val visibilityIcon = if (isPasswordVisible)
                                    painterResource(id = R.drawable.ic_hide_password)
                                else
                                    painterResource(id = R.drawable.ic_show_password)

                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        painter = visibilityIcon,
                                        contentDescription = "Toggle Password Visibility",
                                        modifier = Modifier.size(20.dp),
                                        tint = colorScheme.primary
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (emailInput.isNotBlank() && isValidPassword(passwordInput)) {
                                        loginWithFirebase(
                                            emailInput,
                                            passwordInput,
                                            navController,
                                            snackbarHostState,
                                            coroutineScope
                                        ) { loading = it }
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            textStyle = LocalTextStyle.current.copy(color = colorScheme.onSurface)
                        )

                        if (passwordInput.isNotEmpty() && !isValidPassword(passwordInput)) {
                            Text(
                                text = "Password must be 6–10 characters and include 1 symbol",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(start = 4.dp, bottom = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (emailInput.isNotBlank() && passwordInput.isNotBlank()) {
                                    if (!isValidEmail(emailInput)) {
                                        emailError = true
                                    } else if (!isValidPassword(passwordInput)) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                "Password must be 6–10 characters and should contain atleast one symbol"
                                            )
                                        }
                                    } else {
                                        loginWithFirebase(
                                            emailInput,
                                            passwordInput,
                                            navController,
                                            snackbarHostState,
                                            coroutineScope
                                        ) { loading = it }
                                    }
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Please enter the required fields")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                            enabled = !loading
                        ) {
                            Text("Login", fontSize = 16.sp, color = colorScheme.onPrimary)
                        }

                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }

            // Full-screen loading overlay
            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            }
        }
    }
}

// Firebase Authentication logic
fun loginWithFirebase(
    email: String,
    password: String,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    setLoading: (Boolean) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    setLoading(true)

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            setLoading(false)
            if (task.isSuccessful) {
                navController.navigate("admin") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Login failed, please try again!")
                }
            }
        }
}

@Preview(showBackground = true, name = "Login Screen")
@Composable
fun PreviewLoginScreen() {
    val navController = rememberNavController()
    LoginScreen(navController)
}