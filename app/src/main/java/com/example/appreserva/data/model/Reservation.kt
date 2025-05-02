
package com.example.appreserva.data.model

data class Reservation(
    val userId: String = "",
    val roomId: String = "",
    val date: String = "",
    val time: String = "",
    val material: String = "",
    val notes: String = "",
    val professor: String = "",
    val anotacoes: String = ""
)

