package com.grupo06.doggoapp.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo06.doggoapp.domain.usecase.TokenUseCases
import com.grupo06.doggoapp.presentation.event.EventBus
import com.grupo06.doggoapp.presentation.event.UiEvent
import com.grupo06.doggoapp.presentation.screens.registro.RegistroUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(private val tokenUseCases: TokenUseCases) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState = _uiState.asStateFlow()

    fun registrar(
        nombres: String,
        apellidos: String,
        email: String,
        password: String,
        confirmarPassword: String,
        aceptaPolitica: Boolean
    ) {
        val errorValidacion = validar(nombres, apellidos, email, password, confirmarPassword, aceptaPolitica)
        if (errorValidacion != null) {
            _uiState.value = _uiState.value.copy(error = errorValidacion)
            viewModelScope.launch { EventBus.send(UiEvent.Warning(errorValidacion)) }
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                tokenUseCases.register(nombres.trim(), apellidos.trim(), email.trim(), password, aceptaPolitica)
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                EventBus.send(UiEvent.Success("Registro exitoso"))
            } catch (e: Exception) {
                val mensaje = e.message ?: "No se pudo crear la cuenta"
                _uiState.value = _uiState.value.copy(isLoading = false, error = mensaje)
                EventBus.send(UiEvent.Error(mensaje))
            }
        }
    }

    private fun validar(
        nombres: String,
        apellidos: String,
        email: String,
        password: String,
        confirmarPassword: String,
        aceptaPolitica: Boolean
    ): String? {
        return when {
            nombres.isBlank() -> "Ingresa tus nombres"
            apellidos.isBlank() -> "Ingresa tus apellidos"
            email.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Ingresa un correo electrónico válido"
            password.length < 8 -> "La contraseña debe tener mínimo 8 caracteres"
            !password.any { it.isLetter() } || !password.any { it.isDigit() } ->
                "La contraseña debe combinar letras y números"
            password != confirmarPassword -> "Las contraseñas no coinciden"
            !aceptaPolitica -> "Debes aceptar la política para continuar"
            else -> null
        }
    }
}
