package com.example.gki_noteapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // Chứa danh sách ghi chú (bao gồm id và dữ liệu)
    var notes by remember { mutableStateOf(listOf<Pair<String, Map<String, String>>>()) }

    // Lắng nghe thay đổi từ Firestore
    DisposableEffect(Unit) {
        val listener: ListenerRegistration = db.collection("notes")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    notes = snapshot.documents.map { doc ->
                        doc.id to mapOf(
                            "title" to (doc["title"] as? String ?: "No Title"),
                            "content" to (doc["content"] as? String ?: "No Content"),
                            "imagePath" to (doc["imagePath"] as? String ?: "")
                        )
                    }
                }
            }
        onDispose {
            listener.remove()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { context.startActivity(Intent(context, AddNoteActivity::class.java)) },
                shape = CircleShape,
                containerColor = Color(0xFF00BCD4)
            ) {
                Text("+", color = Color.White, fontSize = 24.sp)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo2),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(40.dp).padding(end = 8.dp)
                        )
                        Text("NoteApp", fontSize = 32.sp, color = Color(0xFF00BCD4))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        auth.signOut()
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = Color(0xFF00BCD4))
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            notes.forEach { (id, note) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            val intent = Intent(context, EditNoteActivity::class.java)
                            intent.putExtra("noteId", id)
                            intent.putExtra("title", note["title"] ?: "")
                            intent.putExtra("content", note["content"] ?: "")
                            intent.putExtra("imagePath", note["imagePath"] ?: "")
                            context.startActivity(intent)
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF297FF1))
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        // Hiển thị ảnh nếu có
                        note["imagePath"]?.let { imagePath ->
                            if (imagePath.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(Uri.parse(imagePath)),
                                    contentDescription = "Note Image",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(end = 8.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Column {
                            Text(note["title"] ?: "No Title", color = Color.White, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(note["content"] ?: "No Content", color = Color.LightGray, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}



}
