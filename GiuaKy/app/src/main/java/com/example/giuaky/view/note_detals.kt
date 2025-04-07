package com.example.giuaky.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.giuaky.model.Note
import com.example.giuaky.model.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID
import java.lang.Exception

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsScreen(navController: NavController, noteId: String?, repository: FirestoreRepository) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val isEditing = noteId != "new"
    val coroutineScope = rememberCoroutineScope()

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId == null) {
        navController.popBackStack()
        return
    }

    LaunchedEffect(noteId) {
        if (isEditing) {
            coroutineScope.launch {
                try {
                    val note = repository.getNoteById(noteId!!)
                    if (note != null && note.userId == userId) {
                        title = note.title
                        description = note.description
                    } else {
                        navController.popBackStack()
                    }
                } catch (e: Exception) {
                    Log.e("NoteDetailsScreen", "Error fetching note", e)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isEditing) "Chỉnh sửa ghi chú" else "Ghi chú mới",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                try {
                                    repository.deleteNote(noteId!!)
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    Log.e("NoteDetailsScreen", "Error deleting note", e)
                                }
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Note")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tiêu đề") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Mô tả") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            if (isEditing) {
                                repository.updateNote(Note(noteId!!, title, description, userId))
                            } else {
                                repository.addNote(Note(UUID.randomUUID().toString(), title, description, userId))
                            }
                            navController.popBackStack()
                        } catch (e: Exception) {
                            Log.e("NoteDetailsScreen", "Error saving note", e)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (isEditing) "Cập nhật" else "Lưu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}