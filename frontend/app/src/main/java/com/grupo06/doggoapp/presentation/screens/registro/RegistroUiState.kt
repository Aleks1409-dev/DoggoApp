package com.grupo06.doggoapp.presentation.screens.registro

data class RegistroUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
