package com.example.appreserva.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class ReservationRepository(private val db: FirebaseFirestore) {

    fun bookReservation(reservationData: Map<String, Any>): Task<Void> {
        val reservationId = db.collection("reservations").document().id
        return db.collection("reservations").document(reservationId)
            .set(reservationData)
    }
}