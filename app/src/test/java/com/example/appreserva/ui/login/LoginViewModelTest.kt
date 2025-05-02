package com.example.appreserva.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.appreserva.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.example.appreserva.util.FakeSuccessAuthTask
import com.example.appreserva.util.FakeFailureAuthTask
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel
    private lateinit var firebaseAuth: FirebaseAuth

    @Before
    fun setup() {
        firebaseAuth = mock()
        authRepository = AuthRepository(firebaseAuth)
        viewModel = LoginViewModel(authRepository)
    }

    @Test
    fun `login success updates loginResult to true`() {
        val email = "teste@email.com"
        val senha = "123456"

        whenever(firebaseAuth.signInWithEmailAndPassword(email, senha))
            .thenReturn(FakeSuccessAuthTask())

        viewModel.login(email, senha)

        viewModel.loginResult.observeForever {
            assertEquals(true, it)
        }
    }

    @Test
    fun `login failure updates errorMessage`() {
        val email = "erro@email.com"
        val senha = "errado"
        val exception = Exception("Credenciais inválidas")

        whenever(firebaseAuth.signInWithEmailAndPassword(email, senha))
            .thenReturn(FakeFailureAuthTask(exception))

        viewModel.login(email, senha)

        viewModel.errorMessage.observeForever {
            assertEquals("Credenciais inválidas", it)
        }
    }
}



