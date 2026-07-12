package com.grupo06.doggoapp.presentation.screens.cuidadorDetalle

import com.grupo06.doggoapp.domain.model.Cuidador

/**
 * Estados concretos de la pantalla de detalle de cuidador.
 */
sealed interface CuidadorDetalleEstado {
    data object Loading : CuidadorDetalleEstado
    data class Success(val cuidador: Cuidador) : CuidadorDetalleEstado
    data class Error(val mensaje: String) : CuidadorDetalleEstado
}

/**
 * Estado completo de la pantalla de detalle de cuidador.
 */
data class CuidadorDetalleUiState(
    val estado: CuidadorDetalleEstado = CuidadorDetalleEstado.Loading,
    val esFavorito: Boolean = false
)
