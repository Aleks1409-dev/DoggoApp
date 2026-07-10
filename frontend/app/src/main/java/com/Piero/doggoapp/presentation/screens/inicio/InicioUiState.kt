package com.piero.doggoapp.presentation.screens.inicio

data class InicioUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)