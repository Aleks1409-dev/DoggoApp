package com.grupo06.doggoapp.domain.model

/**
 * Slot de disponibilidad de un cuidador.
 *
 * Proviene de GET /schedule/{sitterId} y se agrupa por fecha para mostrar
 * las opciones de fecha y rango horario en la pantalla de reserva.
 */
data class Slot(
    val fecha: String,
    val rango: String
)
