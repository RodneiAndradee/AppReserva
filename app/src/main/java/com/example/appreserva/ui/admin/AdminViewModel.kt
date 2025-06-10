package com.example.appreserva.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appreserva.data.model.Reservation // Importe a classe Reservation
import com.example.appreserva.data.repository.ReservationRepository

class AdminViewModel(private val reservationRepository: ReservationRepository) : ViewModel() {

    private val _pendingReservations = MutableLiveData<List<Reservation>>()
    val pendingReservations: LiveData<List<Reservation>> = _pendingReservations

    private val _actionResult = MutableLiveData<Pair<Boolean, String>>() // Pair: <Success, Message>
    val actionResult: LiveData<Pair<Boolean, String>> = _actionResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchPendingReservations()
    }

    fun fetchPendingReservations() {
        _isLoading.value = true
        reservationRepository.getPendingReservations()
            .addOnSuccessListener { querySnapshot ->
                val reservations = querySnapshot.documents.mapNotNull { document ->
                    // Mapeia o documento do Firestore para um objeto Reservation, incluindo o ID do documento
                    document.toObject(Reservation::class.java)?.copy(id = document.id) // O campo 'id' na Reservation.kt é essencial aqui
                }
                _pendingReservations.value = reservations
                _isLoading.value = false
            }
            .addOnFailureListener { exception ->
                _actionResult.value = Pair(false, "Erro ao carregar solicitações: ${exception.message}")
                _isLoading.value = false
            }
    }

    fun approveReservation(reservationId: String) {
        reservationRepository.updateReservationStatus(reservationId, "approved") // Define o status como "approved"
            .addOnSuccessListener {
                _actionResult.value = Pair(true, "Reserva aprovada com sucesso!")
                fetchPendingReservations() // Recarrega a lista para remover a reserva aprovada
            }
            .addOnFailureListener { exception ->
                _actionResult.value = Pair(false, "Erro ao aprovar reserva: ${exception.message}")
            }
    }

    fun rejectReservation(reservationId: String) {
        reservationRepository.updateReservationStatus(reservationId, "rejected") // Define o status como "rejected"
            .addOnSuccessListener {
                _actionResult.value = Pair(true, "Reserva rejeitada com sucesso!")
                fetchPendingReservations() // Recarrega a lista para remover a reserva rejeitada
            }
            .addOnFailureListener { exception ->
                _actionResult.value = Pair(false, "Erro ao rejeitar reserva: ${exception.message}")
            }
    }
}