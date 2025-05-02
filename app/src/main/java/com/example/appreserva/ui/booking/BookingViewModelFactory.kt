package com.example.appreserva.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appreserva.data.repository.ReservationRepository

class BookingViewModelFactory(private val repository: ReservationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
            return BookingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}