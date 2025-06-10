package com.example.appreserva.data.model

import com.google.firebase.firestore.DocumentId

data class Reservation(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val roomId: String = "",
    val date: String = "",
    val time: String = "",
    val material: String = "",
    val notes: String = "",
    val professor: String = "",
    val anotacoes: String = "",
    val status: String = "pending"
)