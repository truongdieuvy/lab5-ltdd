package com.example.giuaky.control

sealed class Screen(val rout: String) {
    object login : Screen("login")
    object register : Screen("register")
    object home : Screen("home")
    object addnote : Screen("addnote")
    object note_details : Screen("note_details")
}


