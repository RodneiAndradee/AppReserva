package com.example.appreserva.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appreserva.data.model.Reservation
import com.example.appreserva.data.repository.ReservationRepository

class ApprovedRejectedBookingsViewModel(private val reservationRepository: ReservationRepository) : ViewModel() {

    private val _allReservations = MutableLiveData<List<Reservation>>()
    val allReservations: LiveData<List<Reservation>> = _allReservations

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchAllReservations()
    }

    fun fetchAllReservations() {
        _isLoading.value = true
        reservationRepository.getAllReservations() // Chama o novo método no ReservationRepository
            .addOnSuccessListener { querySnapshot ->
                val reservations = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Reservation::class.java)?.copy(id = document.id)
                }
                _allReservations.value = reservations
                _isLoading.value = false
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = "Erro ao carregar reservas: ${exception.message}"
                _isLoading.value = false
            }
    }
}