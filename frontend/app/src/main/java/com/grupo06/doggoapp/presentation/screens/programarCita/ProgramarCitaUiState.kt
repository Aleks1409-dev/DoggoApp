package com.grupo06.doggoapp.presentation.screens.programarCita

import com.grupo06.doggoapp.domain.model.Servicio
import com.grupo06.doggoapp.domain.model.Slot

/**
 * Estados concretos de la pantalla de programar cita.
 */
sealed interface ProgramarCitaEstado {
    data object Loading : ProgramarCitaEstado
    data class DisponibilidadCargada(
        val slots: List<Slot>,
        val serviciosDelCuidador: List<Servicio>
    ) : ProgramarCitaEstado

    data object Confirmando : ProgramarCitaEstado
    data class Exito(val mensaje: String) : ProgramarCitaEstado
    data class Error(val mensaje: String) : ProgramarCitaEstado
}

/**
 * Estado completo de la pantalla de programar cita.
 *
 * Incluye las selecciones del usuario y el estado de carga/confirmación.
 */
data class ProgramarCitaUiState(
    val estado: ProgramarCitaEstado = ProgramarCitaEstado.Loading,
    val nombreCuidador: String = "",
    val servicioSeleccionado: Servicio? = null,
    val fechaSeleccionada: String? = null,
    val rangoSeleccionado: String? = null
)
