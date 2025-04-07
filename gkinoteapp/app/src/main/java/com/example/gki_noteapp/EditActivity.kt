package com.example.gki_noteapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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

class EditNoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteId = intent.getStringExtra("noteId") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val content = intent.getStringExtra("content") ?: ""
        setContent {
            EditNoteScreen(noteId, title, content)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(noteId: String, initialTitle: String, initialContent: String) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Edit Note", fontSize = 32.sp, color = Color(0xFF00BCD4))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, HomeActivity::class.java))
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF00BCD4))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        deleteNoteFromFirestore(db, noteId, context)
                        context.startActivity(Intent(context, HomeActivity::class.java))
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
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
                label = { Text("Title", color = Color(0xFF00BCD4)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00BCD4),
                    unfocusedBorderColor = Color(0xFF00BCD4),
                    cursorColor = Color(0xFF00BCD4)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Description", color = Color(0xFF00BCD4)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00BCD4),
                    unfocusedBorderColor = Color(0xFF00BCD4),
                    cursorColor = Color(0xFF00BCD4)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    updateNoteInFirestore(db, noteId, title, content)
                    context.startActivity(Intent(context, HomeActivity::class.java))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4))
            ) {
                Text("Update Note", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

fun updateNoteInFirestore(db: FirebaseFirestore, noteId: String, title: String, content: String) {
    db.collection("notes").document(noteId).update("title", title, "content", content)
}

fun deleteNoteFromFirestore(db: FirebaseFirestore, noteId: String, context: Context) {
    // Lấy dữ liệu ghi chú để tìm imagePath
    db.collection("notes").document(noteId).get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val imagePath = document.getString("imagePath") ?: ""

                // Xóa ghi chú
                db.collection("notes").document(noteId).delete()
                    .addOnSuccessListener {
                        println("Ghi chú đã được xóa thành công!")

                        // Xóa ảnh nếu tồn tại
                        if (imagePath.isNotEmpty()) {
                            val imageFile = File(imagePath)
                            if (imageFile.exists() && imageFile.delete()) {
                                println("Ảnh đã được xóa thành công!")
                            } else {
                                println("Không thể xóa ảnh hoặc ảnh không tồn tại!")
                            }
                        }
                    }
                    .addOnFailureListener {
                        println("Lỗi khi xóa ghi chú: \${it.message}")
                    }
            }
        }
        .addOnFailureListener {
            println("Lỗi khi lấy ghi chú: \${it.message}")
        }
}