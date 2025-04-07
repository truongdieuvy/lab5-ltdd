package com.example.giuaky.control

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.giuaky.view.LoginScreen
import com.example.giuaky.view.RegisterScreen
import com.example.giuaky.model.FirestoreRepository
import com.example.giuaky.view.AddNoteScreen
import com.example.giuaky.view.HomeScreen
import com.example.giuaky.view.NoteDetailsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val repository = FirestoreRepository()

    NavHost(
        navController = navController,
        startDestination = Screen.login.rout
    ) {
        composable(Screen.login.rout) {
            LoginScreen(navController = navController)
        }
        composable(Screen.register.rout) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.home.rout) {
            HomeScreen(navController = navController, repository = repository)
        }
        composable(Screen.addnote.rout) {
            AddNoteScreen(navController = navController, repository = repository)
        }
        composable(Screen.note_details.rout + "/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: "new"
            NoteDetailsScreen(
                navController = navController,
                noteId = noteId,
                repository = repository
            )
        }
    }
}
