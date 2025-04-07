package com.example.gki_noteapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo Firebase Auth
        auth = FirebaseAuth.getInstance()

        setContent {
            RegisterScreen(auth, onRegisterSuccess = {
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Kết thúc RegisterActivity
            })
        }
    }
}

@Composable
fun RegisterScreen(auth: FirebaseAuth, onRegisterSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Ô nhập email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("User ID") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ô nhập password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Hiển thị lỗi nếu có
            errorMessage?.let {
                Text(text = it, color = Color.Red, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Nút Đăng ký
            Button(
                onClick = {
                    registerUser(auth, email, password, onRegisterSuccess) {
                        errorMessage = it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF04CADC))
            ) {
                Text("REGISTER", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nút quay lại đăng nhập
            Button(
                onClick = onRegisterSuccess,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Back to Login", color = Color.White)
            }
        }
    }
}

// Hàm đăng ký người dùng mới
private fun registerUser(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        onError("Email và Password không được để trống")
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Đăng ký thành công!")
                onSuccess()
            } else {
                onError("Đăng ký thất bại: ${task.exception?.message}")
            }
        }
}
