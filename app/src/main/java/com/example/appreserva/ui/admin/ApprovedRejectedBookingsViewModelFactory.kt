package com.example.appreserva.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appreserva.data.repository.ReservationRepository

class ApprovedRejectedBookingsViewModelFactory(private val repository: ReservationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApprovedRejectedBookingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ApprovedRejectedBookingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}