package com.example.appreserva.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ReservationTest {

    @Test
    fun `Reservation data class should hold correct values`() {
        val reservation = Reservation(
            id = "res1",
            userId = "test@example.com",
            roomId = "Sala 101",
            date = "2025-07-01",
            time = "09:00 - 10:00",
            material = "Introdução à Kotlin",
            notes = "Reunião de projeto",
            professor = "Dr. Smith",
            status = "pending"
        )

        assertEquals("res1", reservation.id)
        assertEquals("test@example.com", reservation.userId)
        assertEquals("Sala 101", reservation.roomId)
        assertEquals("2025-07-01", reservation.date)
        assertEquals("09:00 - 10:00", reservation.time)
        assertEquals("Introdução à Kotlin", reservation.material)
        assertEquals("Reunião de projeto", reservation.notes)
        assertEquals("Dr. Smith", reservation.professor)
        assertEquals("pending", reservation.status)
    }

    @Test
    fun `Reservation data class equals should work correctly`() {
        val reservation1 = Reservation(id = "res1", roomId = "Sala 101", professor = "Dr. Smith")
        val reservation2 = Reservation(id = "res1", roomId = "Sala 101", professor = "Dr. Smith")
        val reservation3 = Reservation(id = "res2", roomId = "Sala 101", professor = "Dr. Smith")

        assertEquals(reservation1, reservation2)
        assertNotEquals(reservation1, reservation3)
    }
}