package com.example.appreserva.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "professor" // "professor" or "admin"
)