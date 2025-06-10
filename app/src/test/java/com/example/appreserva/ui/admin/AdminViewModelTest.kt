package com.example.appreserva.ui.admin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.appreserva.data.model.Reservation
import com.example.appreserva.data.repository.ReservationRepository
import com.example.appreserva.util.FakeFailureTask
import com.example.appreserva.util.FakeSuccessTask
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.Mockito.verify
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doAnswer
import org.mockito.ArgumentMatchers.any as anyMockitoArg
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.eq


class AdminViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule() // Necessário para testar LiveData

    private lateinit var mockReservationRepository: ReservationRepository // Usando lateinit var com mock()
    private lateinit var adminViewModel: AdminViewModel

    @Before
    fun setup() {
        mockReservationRepository = mock()

        val mockInitialQuerySnapshot = mock<QuerySnapshot>()
        whenever(mockInitialQuerySnapshot.documents).thenReturn(emptyList())
        whenever(mockReservationRepository.getPendingReservations())
            .thenReturn(mockSuccessTask(mockInitialQuerySnapshot))

        adminViewModel = AdminViewModel(mockReservationRepository)
    }

    @Test
    fun `fetchPendingReservations should update pendingReservations LiveData on success`() {
        // Given
        val reservations = listOf(
            Reservation(id = "req1", roomId = "Sala 1", status = "pending"),
            Reservation(id = "req2", roomId = "Sala 2", status = "pending")
        )

        val mockQuerySnapshot = mock<QuerySnapshot>()
        whenever(mockQuerySnapshot.documents).thenReturn(reservations.map { reservation ->
            val mockDoc = mock<com.google.firebase.firestore.DocumentSnapshot>() // Mockar DocumentSnapshot
            whenever(mockDoc.id).thenReturn(reservation.id) // Definir o ID do documento
            whenever(mockDoc.toObject(Reservation::class.java)).thenReturn(reservation) // Retornar o objeto Reservation
            mockDoc
        })

        whenever(mockReservationRepository.getPendingReservations())
            .thenReturn(mockSuccessTask(mockQuerySnapshot)) // Usar uma função auxiliar mockSuccessTask

        // When
        adminViewModel.fetchPendingReservations()

        // Then
        assertEquals(reservations.size, adminViewModel.pendingReservations.value?.size)
        assertEquals("req1", adminViewModel.pendingReservations.value?.get(0)?.id)
        assertEquals("req2", adminViewModel.pendingReservations.value?.get(1)?.id)
        assertEquals(false, adminViewModel.isLoading.value) // Verifica se o loading foi desativado
    }

    @Test
    fun `fetchPendingReservations should post error message on failure`() {
        val errorMessage = "Erro de rede"
        val exception = Exception(errorMessage)

        whenever(mockReservationRepository.getPendingReservations())
            .thenReturn(mockFailureTask(exception))

        adminViewModel.fetchPendingReservations()


        adminViewModel.actionResult.observeForever {
            // A asserção deve verificar o Pair(false, "Sua Mensagem de Erro")
            assertEquals(Pair(false, "Erro ao carregar solicitações: ${exception.message}"), it)
        }
        assertEquals(false, adminViewModel.isLoading.value)
    }

    @Test
    fun `approveReservation should update status and refetch reservations on success`() {
        val reservationId = "testId"
        val errorMessage = "Falha ao aprovar"
        val exception = Exception(errorMessage)

        whenever(mockReservationRepository.updateReservationStatus(reservationId, "approved"))
            .thenReturn(mockFailureTask(exception))

        // Mock a segunda chamada para getPendingReservations (que ocorre após a atualização)
        // Retornar um FakeSuccessTask que simula QuerySnapshot vazio
        val mockEmptyQuerySnapshot = mock<QuerySnapshot>()
        whenever(mockEmptyQuerySnapshot.documents).thenReturn(emptyList()) // Sem documentos
        whenever(mockReservationRepository.getPendingReservations())
            .thenReturn(mockSuccessTask(mockEmptyQuerySnapshot))

        // When
        adminViewModel.approveReservation(reservationId)

        // Then
        verify(mockReservationRepository).updateReservationStatus(reservationId, "approved") // Verifica se o método de atualização foi chamado
        adminViewModel.actionResult.observeForever {
            assertEquals(Pair(true, "Reserva aprovada com sucesso!"), it)
        }
        verify(mockReservationRepository).getPendingReservations() // Verifica se a lista foi recarregada
    }

    @Test
    fun `approveReservation should post error message on failure`() {
        // Given
        val reservationId = "testId"
        val errorMessage = "Falha ao aprovar"
        val exception = Exception(errorMessage)
        whenever(mockReservationRepository.updateReservationStatus(reservationId, "approved"))
            .thenReturn(mockFailureTask(exception))

        // When
        adminViewModel.approveReservation(reservationId)

        // Then
        adminViewModel.actionResult.observeForever {
            assertEquals(Pair(false, "Erro ao aprovar reserva: ${errorMessage}"), it)
        }
        verify(mockReservationRepository, never()).getPendingReservations() // Não deve recarregar a lista se falhar
    }

    @Test
    fun `rejectReservation should update status and refetch reservations on success`() {
        // Given
        val reservationId = "testId"
        val errorMessage = "Falha ao rejeitar"
        val exception = Exception(errorMessage)
        whenever(mockReservationRepository.updateReservationStatus(reservationId, "rejected"))
            .thenReturn(mockFailureTask(exception))

        val mockEmptyQuerySnapshot = mock<QuerySnapshot>()
        whenever(mockEmptyQuerySnapshot.documents).thenReturn(emptyList())
        whenever(mockReservationRepository.getPendingReservations())
            .thenReturn(mockSuccessTask(mockEmptyQuerySnapshot))

        // When
        adminViewModel.rejectReservation(reservationId)

        // Then
        verify(mockReservationRepository).updateReservationStatus(reservationId, "rejected")
        adminViewModel.actionResult.observeForever {
            assertEquals(Pair(true, "Reserva rejeitada com sucesso!"), it)
        }
        verify(mockReservationRepository).getPendingReservations()
    }

    @Test
    fun `rejectReservation should post error message on failure`() {
        // Given
        val reservationId = "testId"
        val errorMessage = "Falha ao rejeitar"
        whenever(mockReservationRepository.updateReservationStatus(reservationId, "rejected"))
            .thenReturn(FakeFailureTask() as Task<Void>) //

        // When
        adminViewModel.rejectReservation(reservationId)

        // Then
        adminViewModel.actionResult.observeForever {
            assertEquals(Pair(false, "Erro ao rejeitar reserva: ${errorMessage}"), it)
        }
        verify(mockReservationRepository, never()).getPendingReservations()
    }

    // Função auxiliar para criar um mock de Task<QuerySnapshot> de sucesso
    // Adapta-se ao seu FakeSuccessTask que não leva argumentos de resultado
    private fun <T> mockSuccessTask(result: T?): Task<T> {
        val mockTask = mock<Task<T>>()
        // Quando addOnSuccessListener for chamado, simular sucesso
        whenever(mockTask.addOnSuccessListener(any<com.google.android.gms.tasks.OnSuccessListener<in T>>())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as com.google.android.gms.tasks.OnSuccessListener<T>
            listener.onSuccess(result as T)
            mockTask // Retornar a própria mockTask para permitir encadeamento
        }
        // Quando addOnCompleteListener for chamado, simular sucesso
        whenever(mockTask.addOnCompleteListener(any<com.google.android.gms.tasks.OnCompleteListener<T>>())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as com.google.android.gms.tasks.OnCompleteListener<T>
            // Criar um mock Task para o listener, pois o onComplete espera uma Task que pode ser verificada
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

    // Função auxiliar para criar um mock de Task<QuerySnapshot> de falha
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