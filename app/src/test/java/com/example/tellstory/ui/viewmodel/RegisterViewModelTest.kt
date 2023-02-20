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
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var tellStoryRepository: TellStoryRepository

    private lateinit var registerViewModel: RegisterViewModel

    @Test
    fun `register status should return success`() = runTest {

        val name = "Test User"
        val email = "test@example.com"
        val password = "testpassword"
        val message = true
        Mockito.`when`(tellStoryRepository.register(name, email, password)).thenReturn(message)

        registerViewModel = RegisterViewModel(tellStoryRepository)
        registerViewModel.newRegister(name, email, password)

    }

}