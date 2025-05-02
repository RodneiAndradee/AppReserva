package com.example.appreserva.data.repository

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertNotNull

class ReservationRepositoryTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference
    private lateinit var documentReference: DocumentReference
    private lateinit var repository: ReservationRepository

    @Before
    fun setup() {
        firestore = mock()
        collectionReference = mock()
        documentReference = mock()

        whenever(firestore.collection("reservations")).thenReturn(collectionReference)
        whenever(collectionReference.document()).thenReturn(documentReference)
        whenever(collectionReference.document(any())).thenReturn(documentReference)
        whenever(documentReference.id).thenReturn("fakeId")
        whenever(documentReference.set(any())).thenReturn(Tasks.forResult(null))

        repository = ReservationRepository(firestore)
    }

    @Test
    fun `bookReservation should return non-null Task`() {
        val data = mapOf(
            "userId" to "user1",
            "roomId" to "room1",
            "date" to "2025-05-01",
            "time" to "15:00 - 16:00",
            "material" to "Notebook",
            "notes" to "Texto",
            "professor" to "user1",
            "anotacoes" to "Nota"
        )

        val result: Task<Void> = repository.bookReservation(data)

        assertNotNull(result)
    }
}


