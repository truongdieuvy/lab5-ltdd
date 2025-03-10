package com.example.firebase

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen() {
    val db = FirebaseFirestore.getInstance()
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = text,
            onValueChange = { newText -> text = newText },
            label = { Text(text = "Enter your data") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val data = hashMapOf("message" to text)
                db.collection("messages")
                    .add(data)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Firebase", "Document added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Error adding document", e)
                    }
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Submit")
        }
    }
}
