package com.example.gki_noteapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import androidx.activity.compose.rememberLauncherForActivityResult


class AddNoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddNoteScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen() {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Cho phép chọn ảnh từ mọi nguồn
    val selectImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add New Note", fontSize = 32.sp, color = Color(0xFF00BCD4)) },
                navigationIcon = {
                    IconButton(onClick = { context.startActivity(Intent(context, HomeActivity::class.java)) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF00BCD4))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFBFE1DF))
            )
        },
        containerColor = Color(0xFFBFE1DF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00BCD4),
                    unfocusedBorderColor = Color(0xFF00BCD4)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00BCD4),
                    unfocusedBorderColor = Color(0xFF00BCD4)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { selectImageLauncher.launch(arrayOf("image/*")) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4))
            ) {
                Text("Select Image", color = Color.White)
            }

            imageUri?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Selected: ${it.lastPathSegment}")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val imagePath = imageUri?.let { saveImageLocally(context, it) } ?: ""
                    addNoteToFirestore(db, title, content, imagePath, context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
                modifier = Modifier
                    .padding(8.dp)
                    .height(60.dp)
                    .fillMaxWidth(0.7f)
            ) {
                Text("Add Note", color = Color.White, fontSize = 20.sp)
            }
        }
    }
}

// Lưu ảnh vào bộ nhớ trong
fun saveImageLocally(context: Context, uri: Uri): String {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val file = File(context.filesDir, "image_${System.currentTimeMillis()}.jpg")
    inputStream?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file.absolutePath
}

// Thêm ghi chú vào Firestore
fun addNoteToFirestore(
    db: FirebaseFirestore,
    title: String,
    content: String,
    imagePath: String,
    context: Context
) {
    if (title.isNotBlank() && content.isNotBlank()) {
        val note = hashMapOf(
            "title" to title,
            "content" to content,
            "imagePath" to imagePath
        )

        db.collection("notes")
            .add(note)
            .addOnSuccessListener {
                println("Ghi chú đã được thêm thành công!")
                context.startActivity(Intent(context, HomeActivity::class.java))
            }
            .addOnFailureListener {
                println("Lỗi khi thêm ghi chú: ${it.message}")
            }
    } else {
        println("Tiêu đề và nội dung không được để trống!")
    }
}