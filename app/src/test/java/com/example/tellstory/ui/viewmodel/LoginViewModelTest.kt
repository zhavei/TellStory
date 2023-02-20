package com.example.tellstory.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storyapp.helper.MainDispatcherRule
import com.example.tellstory.repository.TellStoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var tellStoryRepository: TellStoryRepository

    private lateinit var loginViewModel: LoginViewModel

    @Test
    fun `login with valid credentials should authenticated`() =
        runTest {
            val email = "test@example.com"
            val password = "testpassword"
            val name = "Test User"
            val expectedToken = "12345"
            Mockito.`when`(tellStoryRepository.login(email, password)).thenReturn(expectedToken)
            Mockito.`when`(tellStoryRepository.getUserName(email, password)).thenReturn(name)

            loginViewModel = LoginViewModel(tellStoryRepository)
            loginViewModel.userLogin(email, password)

        }

    @Test
    fun `login with invalid credentials should not authenticated`() =
        runTest {
            val email = "invalid@example.com"
            val password = "invalidpassword"
            Mockito.`when`(tellStoryRepository.login(email, password)).thenReturn(null)

            loginViewModel = LoginViewModel(tellStoryRepository)
            loginViewModel.userLogin(email, password)

        }

    @Test
    fun `login with an exception should set authenticated to false`() =
        runTest {
            val email = "test@example.com"
            val password = "testpassword"
            Mockito.`when`(
                tellStoryRepository.login(
                    email,
                    password
                )
            ).thenAnswer { throw Exception("Something went wrong") }

            loginViewModel = LoginViewModel(tellStoryRepository)
            loginViewModel.userLogin(email, password)
        }
}