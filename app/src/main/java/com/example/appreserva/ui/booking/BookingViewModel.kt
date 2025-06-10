package com.example.appreserva.ui.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appreserva.data.model.Reservation
import com.example.appreserva.data.repository.ReservationRepository

class BookingViewModel(private val reservationRepository: ReservationRepository) : ViewModel() {

    private val _bookingResult = MutableLiveData<Boolean>()
    val bookingResult: LiveData<Boolean> = _bookingResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun bookReservation(reservation: Reservation) {
        val reservationData = mapOf(
            "userId" to reservation.userId,
            "roomId" to reservation.roomId,
            "date" to reservation.date,
            "time" to reservation.time,
            "material" to reservation.material,
            "notes" to reservation.notes,
            "professor" to reservation.professor,
			"status" to "pending"
        )

        reservationRepository.bookReservation(reservationData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _bookingResult.value = true
                } else {
                    _errorMessage.value = task.exception?.message ?: "Erro ao fazer a reserva."
                }
            }
    }
}

