package com.example.appreserva.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class UserTest {

    @Test
    fun `User data class should hold correct values`() {
        val user = User(
            uid = "uid123",
            email = "admin@example.com",
            role = "admin"
        )

        assertEquals("uid123", user.uid)
        assertEquals("admin@example.com", user.email)
        assertEquals("admin", user.role)
    }

    @Test
    fun `User data class equals should work correctly`() {
        val user1 = User(uid = "uid1", email = "test1@example.com", role = "professor")
        val user2 = User(uid = "uid1", email = "test1@example.com", role = "professor")
        val user3 = User(uid = "uid2", email = "test2@example.com", role = "admin")

        assertEquals(user1, user2)
        assertNotEquals(user1, user3)
    }
}