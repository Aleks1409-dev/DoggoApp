package com.grupo06.doggoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.grupo06.doggoapp.domain.usecase.TokenUseCases
import com.grupo06.doggoapp.presentation.screens.perfil.PerfilUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PerfilViewModel(private val tokenUseCases: TokenUseCases) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState = _uiState.asStateFlow()

    fun cerrarSesion() {
        tokenUseCases.logout()
        _uiState.value = _uiState.value.copy(isLoggedOut = true)
    }
}
