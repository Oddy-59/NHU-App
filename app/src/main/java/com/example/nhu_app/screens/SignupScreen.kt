package com.example.nhu_app.screens

import android.widget.Toast
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nhu_app.R


@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val isPasswordValid = password.length >= 6
    val isEmailValid = email.contains("@") && email.length > 3
    val isFormValid = username.isNotBlank() && isEmailValid && isPasswordValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Icon(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            tint = Color.Black,
            modifier = Modifier
                .size(120.dp)
                .padding(top = 32.dp, bottom = 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 24.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SignUpInputField(
                    label = "Username",
                    value = username,
                    onValueChange = { username = it },
                    iconRes = R.drawable.ic_user
                )

                SignUpInputField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    iconRes = R.drawable.ic_email
                )

                // Email validation warning
                if (email.isNotEmpty() && !isEmailValid) {
                    Text(
                        text = "Email must contain @",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp)
                    )
                }

                SignUpInputField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    iconRes = R.drawable.ic_password,
                    isPassword = true,
                    showPassword = showPassword,
                    onTogglePasswordVisibility = { showPassword = !showPassword }
                )

                // Password validation warning
                if (password.isNotEmpty() && !isPasswordValid) {
                    Text(
                        text = "Password must be at least 6 characters",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isFormValid) {
                            navController.navigate("home")
                        } else {
                            Toast.makeText(context, "Please enter the required fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF015A80),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Create Account", fontSize = 16.sp)
                }
            }
        }
    }
}






@Composable
fun SignUpInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    iconRes: Int,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null
) {
    val primaryColor = Color(0xFF015A80)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { onTogglePasswordVisibility?.invoke() }) {
                    Icon(
                        painter = painterResource(
                            id = if (showPassword) R.drawable.ic_hide_password else R.drawable.ic_show_password
                        ),
                        contentDescription = if (showPassword) "Hide password" else "Show password",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(5.dp),
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black,
            cursorColor = primaryColor,
            focusedLeadingIconColor = primaryColor,
            unfocusedLeadingIconColor = primaryColor,
            focusedTrailingIconColor = primaryColor,
            unfocusedTrailingIconColor = primaryColor
        )
    )
}


@Preview(showBackground = true, name = "SignUp Screen")
@Composable
fun PreviewSignUpScreen() {
    val navController = rememberNavController()
    SignUpScreen(navController = navController)
}