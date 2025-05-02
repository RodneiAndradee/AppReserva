package com.example.appreserva.ui.booking

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.appreserva.data.model.Reservation
import com.example.appreserva.data.repository.ReservationRepository
import com.example.appreserva.util.FakeFailureTask
import com.example.appreserva.util.FakeSuccessTask
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class BookingViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: BookingViewModel
    private lateinit var reservationRepository: ReservationRepository

    @Before
    fun setup() {
        reservationRepository = mock()
        viewModel = BookingViewModel(reservationRepository)
    }

    @Test
    fun `bookReservation should update bookingResult to true on success`() {
        val reservation = Reservation(
            userId = "user1",
            roomId = "room1",
            date = "2025-05-01",
            time = "15:00 - 16:00",
            material = "Notebook",
            notes = "Teste",
            professor = "user1",
            anotacoes = "Teste anotação"
        )

        whenever(reservationRepository.bookReservation(any()))
            .thenReturn(FakeSuccessTask())

        viewModel.bookReservation(reservation)

        viewModel.bookingResult.observeForever {
            assertEquals(true, it)
        }
    }

    @Test
    fun `bookReservation should update errorMessage on failure`() {
        val reservation = Reservation(
            userId = "user1",
            roomId = "room1",
            date = "2025-05-01",
            time = "15:00 - 16:00",
            material = "Notebook",
            notes = "Teste",
            professor = "user1",
            anotacoes = "Teste anotação"
        )

        whenever(reservationRepository.bookReservation(any()))
            .thenReturn(FakeFailureTask())

        viewModel.bookReservation(reservation)

        viewModel.errorMessage.observeForever {
            assertEquals("Erro ao fazer a reserva.", it)
        }
    }
}


