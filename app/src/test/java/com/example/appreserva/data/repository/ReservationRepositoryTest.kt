package com.example.appreserva.data.repository

import com.example.appreserva.data.model.Reservation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.example.appreserva.util.FakeFailureTask // Importar seus fakes
import com.example.appreserva.util.FakeSuccessTask // Importar seus fakes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat // Manter se necessário, mas vou usar captor
import org.mockito.kotlin.capture // Adicionar import para ArgumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.ArgumentCaptor // Adicionar import para ArgumentCaptor
import org.mockito.Captor // Adicionar import para Captor

class ReservationRepositoryTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var mockDocumentReference: DocumentReference
    private lateinit var mockQuery: Query

    @Captor // Captor para capturar o argumento 'map'
    private lateinit var mapCaptor: ArgumentCaptor<Map<String, Any>>

    private lateinit var repository: ReservationRepository

    @Before
    fun setup() {
        // Inicializa mocks com @Mock ou mock()
        mockFirestore = mock()
        mockCollectionReference = mock()
        mockDocumentReference = mock()
        mockQuery = mock()

        // Para ArgumentCaptor
        org.mockito.MockitoAnnotations.openMocks(this)

        whenever(mockFirestore.collection("reservations")).thenReturn(mockCollectionReference)

        // Configurar .document() para retornar um mockDocumentReference com um ID
        whenever(mockCollectionReference.document()).thenReturn(mockDocumentReference)
        whenever(mockDocumentReference.id).thenReturn("test_reservation_id")

        repository = ReservationRepository(mockFirestore)
    }

    @Test
    fun `bookReservation should save reservation data with pending status`() {
        // Given
        val reservationData = mapOf(
            "userId" to "user@test.com",
            "roomId" to "Sala 01",
            "date" to "2025-07-01",
            "time" to "10:00-11:00",
            "material" to "Aula",
            "notes" to "Notas",
            "professor" to "Prof. Teste"
        )
        whenever(mockDocumentReference.set(any<Map<String, Any>>()))
            .thenReturn(FakeSuccessTask() as Task<Void>) //

        // When
        val task = repository.bookReservation(reservationData)

        // Then
        // Usar ArgumentCaptor para verificar o mapa passado
        verify(mockDocumentReference, times(1)).set(capture(mapCaptor))
        val capturedMap = mapCaptor.value
        assertEquals("pending", capturedMap["status"])
        assertEquals("user@test.com", capturedMap["userId"])
        assertEquals("Sala 01", capturedMap["roomId"])
        // Adicione mais asserções para outros campos, se necessário

        assertTrue(task.isComplete)
        assertTrue(task.isSuccessful)
    }

    @Test
    fun `getPendingReservations should return pending reservations`() {
        // Given
        // Mock da consulta que filtra por status "pending"
        whenever(mockCollectionReference.whereEqualTo("status", "pending")).thenReturn(mockQuery)

        // Mock um QuerySnapshot vazio para começar
        val mockQuerySnapshot = mock<QuerySnapshot>()
        whenever(mockQuerySnapshot.documents).thenReturn(emptyList())

        // Use o helper para mockar a Task de sucesso
        whenever(mockQuery.get()).thenReturn(mockSuccessTask(mockQuerySnapshot))

        // When
        val task = repository.getPendingReservations()

        // Then
        verify(mockCollectionReference).whereEqualTo("status", "pending")
        assertNotNull(task)
        assertTrue(task.isComplete)
        assertTrue(task.isSuccessful) // Verifica se a task foi bem-sucedida
    }

    @Test
    fun `getPendingReservations should return failure task on error`() {
        // Given
        val exception = Exception("Erro de conexão")
        whenever(mockCollectionReference.whereEqualTo("status", "pending")).thenReturn(mockQuery)
        whenever(mockQuery.get()).thenReturn(mockFailureTask(exception)) // Usar helper para falha

        // When
        val task = repository.getPendingReservations()

        // Then
        assertNotNull(task)
        assertTrue(task.isComplete)
        assertFalse(task.isSuccessful)
        assertEquals(exception, task.exception)
    }

    @Test
    fun `getAllReservations should return all reservations`() {
        // Given
        val mockQuerySnapshot = mock<QuerySnapshot>()
        whenever(mockQuerySnapshot.documents).thenReturn(emptyList())
        whenever(mockCollectionReference.get()).thenReturn(mockSuccessTask(mockQuerySnapshot)) // Usar helper para sucesso

        // When
        val task = repository.getAllReservations()

        // Then
        assertNotNull(task)
        assertTrue(task.isComplete)
        assertTrue(task.isSuccessful)
    }

    @Test
    fun `getAllReservations should return failure task on error`() {
        // Given
        val exception = Exception("Erro de servidor")
        whenever(mockCollectionReference.get()).thenReturn(mockFailureTask(exception)) // Usar helper para falha

        // When
        val task = repository.getAllReservations()

        // Then
        assertNotNull(task)
        assertTrue(task.isComplete)
        assertFalse(task.isSuccessful)
        assertEquals(exception, task.exception)
    }

    @Test
    fun `updateReservationStatus should update reservation status`() {
        // Given
        val reservationId = "resId123"
        val newStatus = "approved"
        whenever(mockCollectionReference.document(reservationId)).thenReturn(mockDocumentReference)
        whenever(mockDocumentReference.update("status", newStatus))
            .thenReturn(FakeSuccessTask() as Task<Void>) //

        // When
        val task = repository.updateReservationStatus(reservationId, newStatus)

        // Then
        verify(mockDocumentReference).update("status", newStatus)
        assertTrue(task.isComplete)
        assertTrue(task.isSuccessful)
    }

    @Test
    fun `updateReservationStatus should return failure task on error`() {
        // Given
        val reservationId = "resId123"
        val newStatus = "approved"
        val exception = Exception("Falha de atualização")
        whenever(mockCollectionReference.document(reservationId)).thenReturn(mockDocumentReference)
        whenever(mockDocumentReference.update("status", newStatus))
            .thenReturn(FakeFailureTask() as Task<Void>) //

        // When
        val task = repository.updateReservationStatus(reservationId, newStatus)

        // Then
        assertFalse(task.isSuccessful)
        assertTrue(task.isComplete)
        assertEquals(exception, task.exception)
    }


    // Funções auxiliares para mockar Task de sucesso/falha
    // Adaptadas para usar o Mockito-Kotlin e os tipos de Task
    private fun <T> mockSuccessTask(result: T?): Task<T> {
        val mockTask = mock<Task<T>>()
        whenever(mockTask.isSuccessful).thenReturn(true)
        whenever(mockTask.isComplete).thenReturn(true)
        whenever(mockTask.result).thenReturn(result)
        whenever(mockTask.exception).thenReturn(null)

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
        return mockTask
    }

    private fun <T> mockFailureTask(exception: Exception): Task<T> {
        val mockTask = mock<Task<T>>()
        whenever(mockTask.isSuccessful).thenReturn(false)
        whenever(mockTask.isComplete).thenReturn(true)
        whenever(mockTask.exception).thenReturn(exception)
        whenever(mockTask.result).thenReturn(null)

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
        return mockTask
    }
}