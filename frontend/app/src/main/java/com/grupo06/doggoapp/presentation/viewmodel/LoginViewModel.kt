package com.grupo06.doggoapp.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo06.doggoapp.domain.usecase.TokenUseCases
import com.grupo06.doggoapp.presentation.event.EventBus
import com.grupo06.doggoapp.presentation.event.UiEvent
import com.grupo06.doggoapp.presentation.screens.login.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val tokenUseCases: TokenUseCases) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        val errorValidacion = validar(email, password)
        if (errorValidacion != null) {
            _uiState.value = _uiState.value.copy(error = errorValidacion)
            viewModelScope.launch { EventBus.send(UiEvent.Warning(errorValidacion)) }
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                tokenUseCases.login(email.trim(), password)
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                val mensaje = e.message ?: "No se pudo iniciar sesión"
                _uiState.value = _uiState.value.copy(isLoading = false, error = mensaje)
                EventBus.send(UiEvent.Error(mensaje))
            }
        }
    }

    private fun validar(email: String, password: String): String? {
        return when {
            email.isBlank() || password.isBlank() -> "Correo y contraseña son obligatorios"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Ingresa un correo electrónico válido"
            else -> null
        }
    }
}
