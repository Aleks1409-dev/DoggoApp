package com.grupo06.doggoapp.presentation.screens.inicio

import com.grupo06.doggoapp.domain.model.Cuidador

/**
 * Filtros activos en la pantalla de inicio.
 */
data class InicioFiltros(
    val busqueda: String = "",
    val servicioSeleccionado: String? = null,
    val ordenAscendente: Boolean = true
)

/**
 * Estados concretos del listado de cuidadores.
 */
sealed interface InicioEstado {
    data object Loading : InicioEstado
    data class Success(val cuidadores: List<Cuidador>) : InicioEstado
    data object Empty : InicioEstado
    data class Error(val mensaje: String) : InicioEstado
}

/**
 * Estado completo de la pantalla de inicio.
 */
data class InicioUiState(
    val estado: InicioEstado = InicioEstado.Loading,
    val filtros: InicioFiltros = InicioFiltros(),
    val serviciosDisponibles: List<String> = emptyList()
)
