package com.example.appreserva.ui.admin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.appreserva.data.model.Reservation
import com.example.appreserva.data.repository.ReservationRepository
import com.example.appreserva.util.FakeFailureTask // Importar seus fakes
import com.example.appreserva.util.FakeSuccessTask // Importar seus fakes
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ApprovedRejectedBookingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule() // Necessário para testar LiveData

    private lateinit var mockReservationRepository: ReservationRepository
    private lateinit var viewModel: ApprovedRejectedBookingsViewModel

    @Before
    fun setup() {
        mockReservationRepository = mock()
        viewModel = ApprovedRejectedBookingsViewModel(mockReservationRepository)
    }

    @Test
    fun `fetchAllReservations should update allReservations LiveData on success`() {
        // Given
        val reservations = listOf(
            Reservation(id = "res1", roomId = "Sala 1", status = "approved"),
            Reservation(id = "res2", roomId = "Sala 2", status = "rejected"),
            Reservation(id = "res3", roomId = "Sala 3", status = "pending")
        )
        val mockQuerySnapshot = mock<QuerySnapshot>()
        whenever(mockQuerySnapshot.documents).thenReturn(reservations.map { reservation ->
            val mockDoc = mock<com.google.firebase.firestore.DocumentSnapshot>()
            whenever(mockDoc.id).thenReturn(reservation.id)
            whenever(mockDoc.toObject(Reservation::class.java)).thenReturn(reservation)
            mockDoc
        })

        // Quando getAllReservations() for chamado, retornar um FakeSuccessTask com o mockQuerySnapshot
        whenever(mockReservationRepository.getAllReservations())
            .thenReturn(mockSuccessTask(mockQuerySnapshot))

        // When
        viewModel.fetchAllReservations()

        // Then
        assertEquals(3, viewModel.allReservations.value?.size)
        assertEquals("res1", viewModel.allReservations.value?.get(0)?.id)
        assertEquals("res2", viewModel.allReservations.value?.get(1)?.id)
        assertEquals("res3", viewModel.allReservations.value?.get(2)?.id)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `fetchAllReservations should post error message on failure`() {
        // Given
        val errorMessage = "Erro ao carregar todas as reservas"
        whenever(mockReservationRepository.getAllReservations())
            .thenReturn(FakeFailureTask() as Task<QuerySnapshot>) //

        // When
        viewModel.fetchAllReservations()

        // Then
        viewModel.errorMessage.observeForever {
            assertEquals("Erro ao carregar reservas: $errorMessage", it)
        }
        assertEquals(false, viewModel.isLoading.value)
    }

    // Funções auxiliares para mockar Task de sucesso/falha (iguais às do AdminViewModelTest)
    private fun <T> mockSuccessTask(result: T?): Task<T> {
        val mockTask = mock<Task<T>>()
        whenever(mockTask.addOnSuccessListener(any<com.google.android.gms.tasks.OnSuccessListener<in T>>())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as com.google.android.gms.tasks.OnSuccessListener<T>
            listener.onSuccess(result as T)
            mockTask
        }
        whenever(mockTask.addOnCompleteListener(any<com.google.android.gms.tasks.OnCompleteListener<T>>())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as com.google.android.gms.tasks.OnCompleteListener<T>
            val completedTask = mock<Task<T>>()
            whenever(completedTask.isSuccessful).thenReturn(true)
            whenever(completedTask.result).thenReturn(result)
            listener.onComplete(completedTask)
            mockTask
        }
        whenever(mockTask.isSuccessful).thenReturn(true)
        whenever(mockTask.result).thenReturn(result)
        whenever(mockTask.exception).thenReturn(null)
        return mockTask
    }

    private fun <T> mockFailureTask(exception: Exception): Task<T> {
        val mockTask = mock<Task<T>>()
        whenever(mockTask.addOnFailureListener(any<com.google.android.gms.tasks.OnFailureListener>())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as com.google.android.gms.tasks.OnFailureListener
            listener.onFailure(exception)
            mockTask
        }
        whenever(mockTask.addOnCompleteListener(any<com.google.android.gms.tasks.OnCompleteListener<T>>())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as com.google.android.gms.tasks.OnCompleteListener<T>
            val failedTask = mock<Task<T>>()
            whenever(failedTask.isSuccessful).thenReturn(false)
            whenever(failedTask.exception).thenReturn(exception)
            listener.onComplete(failedTask)
            mockTask
        }
        whenever(mockTask.isSuccessful).thenReturn(false)
        whenever(mockTask.exception).thenReturn(exception)
        whenever(mockTask.result).thenReturn(null)
        return mockTask
    }
}