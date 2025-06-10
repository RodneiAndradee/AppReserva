package com.example.appreserva.data.repository

import com.example.appreserva.data.model.Reservation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ReservationRepository(private val db: FirebaseFirestore) {

    fun bookReservation(reservationData: Map<String, Any>): Task<Void> {
        val reservationId = db.collection("reservations").document().id
        val dataWithStatus = reservationData.toMutableMap()
        dataWithStatus["status"] = "pending"
        return db.collection("reservations").document(reservationId)
            .set(dataWithStatus)
    }

    fun getPendingReservations(): Task<QuerySnapshot> {
        return db.collection("reservations")
            .whereEqualTo("status", "pending") // Filtra por status "pending"
            .get()
    }

    fun getAllReservations(): Task<QuerySnapshot> {
        return db.collection("reservations")
            .get() // Busca todos os documentos na coleção 'reservations'
    }

    // NOVO MÉTODO: Atualizar o status de uma reserva
    fun updateReservationStatus(reservationId: String, newStatus: String): Task<Void> {
        return db.collection("reservations")
            .document(reservationId)
            .update("status", newStatus) // Atualiza o campo 'status'
    }
}