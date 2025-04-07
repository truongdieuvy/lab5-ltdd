package com.example.giuaky.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.giuaky.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    val firebaseAuth = Firebase.auth

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.note_logo),
                contentDescription = "Note App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp)
            )

            Text(
                text = "Welcome to NoteApp!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                label = { Text("Email") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF007BFF),
                    unfocusedBorderColor = Color.Gray
                )
            )

            PasswordTextField(
                label = "Password",
                password = password,
                onPasswordChange = { password = it },
                isPasswordVisible = isPasswordVisible,
                onVisibilityChange = { isPasswordVisible = it }
            )

            PasswordTextField(
                label = "Confirm Password",
                password = confirmPassword,
                onPasswordChange = { confirmPassword = it },
                isPasswordVisible = isConfirmPasswordVisible,
                onVisibilityChange = { isConfirmPasswordVisible = it },
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )

            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword == password) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = firebaseAuth.currentUser
                                    user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                        if (verifyTask.isSuccessful) {
                                            Toast.makeText(context, "Verification email sent!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("home")
                                        } else {
                                            Toast.makeText(context, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Please fill in all fields and confirm password correctly!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
            ) {
                Text("Register", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account?")
                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Login now!", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF007BFF))
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialIcon(R.drawable.facebook_icon)
                SocialIcon(R.drawable.google_icon)
            }
        }
    }
}

@Composable
fun PasswordTextField(
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        keyboardActions = keyboardActions,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { onVisibilityChange(!isPasswordVisible) }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Filled.LockOpen else Icons.Filled.Lock,
                    tint = Color(0xFF007BFF),
                    contentDescription = null
                )
            }
        },
        label = { Text(label) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF007BFF),
            unfocusedBorderColor = Color.Gray
        )
    )
}

@Composable
fun SocialIcon(iconRes: Int) {
    Image(
        painter = painterResource(id = iconRes),
        contentDescription = null,
        modifier = Modifier
            .size(50.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp))
            .padding(8.dp)
    )
}

