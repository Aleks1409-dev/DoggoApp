package com.grupo06.doggoapp.presentation.screens.login

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
