package com.grupo06.doggoapp.presentation.navigation

object NavRutas {
    const val BIENVENIDA = "bienvenida"
    const val INICIO = "inicio"
    const val AGENDA = "agenda"
    const val MENSAJES = "mensajes"
    const val PERFIL = "perfil"
    const val CUIDADOR_DETALLE = "cuidador/{sitterId}"
    const val PROGRAMAR_CITA = "programar-cita/{sitterId}"

    fun cuidadorDetalle(sitterId: String): String = "cuidador/$sitterId"
    fun programarCita(sitterId: String): String = "programar-cita/$sitterId"

    fun getTitulo(ruta: String?): String {
        return when {
            ruta == BIENVENIDA -> ""
            ruta == INICIO -> "DoggoApp"
            ruta == AGENDA -> "Mi Agenda"
            ruta == MENSAJES -> "Mensajes"
            ruta == PERFIL -> "Mi Perfil"
            else -> "DoggoApp"
        }
    }
}