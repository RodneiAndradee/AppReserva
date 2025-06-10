package com.example.appreserva.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.example.appreserva.util.FakeFailureAuthTask // Importar seus fakes
import com.example.appreserva.util.FakeSuccessAuthTask // Importar seus fakes
import com.google.firebase.auth.AuthResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthRepositoryTest {

    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        mockFirebaseAuth = mock()
        authRepository = AuthRepository(mockFirebaseAuth)
    }

    @Test
    fun `login should return success task on successful sign in`() {
        // Given
        val email = "test@example.com"
        val password = "password123"
        whenever(mockFirebaseAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(FakeSuccessAuthTask()) //

        // When
        val task = authRepository.login(email, password)

        // Then
        assertTrue(task.isSuccessful)
        assertTrue(task.isComplete)
    }

    @Test
    fun `login should return failure task on sign in failure`() {
        // Given
        val email = "fail@example.com"
        val password = "wrongpassword"
        val exception = Exception("Invalid credentials")
        whenever(mockFirebaseAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(FakeFailureAuthTask(exception)) //

        // When
        val task = authRepository.login(email, password)

        // Then
        assertFalse(task.isSuccessful)
        assertTrue(task.isComplete)
        assertEquals(exception, task.exception)
    }

    @Test
    fun `logout should call signOut on FirebaseAuth`() {
        // When
        authRepository.logout()

        // Then
        verify(mockFirebaseAuth).signOut() // Verifica se signOut foi chamado
    }

    @Test
    fun `isLoggedIn should return true when currentUser is not null`() {
        // Given
        // Mock FirebaseAuth para simular que há um usuário logado
        whenever(mockFirebaseAuth.currentUser).thenReturn(mock())

        // Then
        assertTrue(authRepository.isLoggedIn())
    }

    @Test
    fun `isLoggedIn should return false when currentUser is null`() {
        // Given
        // Mock FirebaseAuth para simular que não há usuário logado
        whenever(mockFirebaseAuth.currentUser).thenReturn(null)

        // Then
        assertFalse(authRepository.isLoggedIn())
    }
}