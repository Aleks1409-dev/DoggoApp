package com.grupo06.doggoapp.presentation.screens.inicio

import com.grupo06.doggoapp.domain.model.Cuidador

data class InicioUiState(
    val cuidadores: List<Cuidador> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
