package com.example.giuaky.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("notes")
    private val auth = FirebaseAuth.getInstance()

    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun addNote(note: Note): Boolean {
        val userId = getCurrentUserId() ?: return false
        return try {
            val noteId = if (note.id.isEmpty()) notesCollection.document().id else note.id
            val newNote = note.copy(id = noteId, userId = userId)
            notesCollection.document(noteId).set(newNote).await()
            Log.d("FirestoreRepository", "Note added successfully: $noteId")
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error adding note: ${e.message}", e)
            false
        }
    }

    suspend fun deleteNote(noteId: String): Boolean {
        val userId = getCurrentUserId() ?: return false
        return try {
            val document = notesCollection.document(noteId).get().await()
            val note = document.toObject(Note::class.java)
            if (note?.userId == userId) {
                notesCollection.document(noteId).delete().await()
                Log.d("FirestoreRepository", "Note deleted successfully: $noteId")
                true
            } else {
                Log.e("FirestoreRepository", "Permission denied: User does not own this note.")
                false
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error deleting note: ${e.message}", e)
            false
        }
    }

    suspend fun updateNote(note: Note): Boolean {
        val userId = getCurrentUserId() ?: return false
        return try {
            if (note.userId == userId) {
                notesCollection.document(note.id).set(note).await()
                Log.d("FirestoreRepository", "Note updated successfully: ${note.id}")
                true
            } else {
                Log.e("FirestoreRepository", "Permission denied: User does not own this note.")
                false
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error updating note: ${e.message}", e)
            false
        }
    }

    suspend fun getNoteById(noteId: String): Note? {
        val userId = getCurrentUserId() ?: return null
        return try {
            val document = notesCollection.document(noteId).get().await()
            val note = document.toObject(Note::class.java)
            if (note?.userId == userId) {
                Log.d("FirestoreRepository", "Fetched note: $noteId")
                note
            } else {
                Log.e("FirestoreRepository", "Permission denied: User does not own this note.")
                null
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error getting note: ${e.message}", e)
            null
        }
    }

    suspend fun getNotes(): List<Note> {
        val userId = getCurrentUserId() ?: return emptyList()
        return try {
            val result = notesCollection.whereEqualTo("userId", userId).get().await()
            val notes = result.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Note::class.java)
                } catch (e: Exception) {
                    Log.e("FirestoreRepository", "Error parsing note: ${e.message}", e)
                    null
                }
            }
            Log.d("FirestoreRepository", "Fetched ${notes.size} notes for user: $userId")
            notes
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error getting notes: ${e.message}", e)
            emptyList()
        }
    }
}
