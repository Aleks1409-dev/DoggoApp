package com.grupo06.doggoapp.presentation.navigation

object NavRutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val BIENVENIDA = "bienvenida"
    const val INICIO = "inicio"
    const val AGENDA = "agenda"
    const val MENSAJES = "mensajes"
    const val PERFIL = "perfil"

    fun getTitulo(ruta: String?): String {
        return when {
            ruta == LOGIN -> ""
            ruta == REGISTRO -> ""
            ruta == BIENVENIDA -> ""
            ruta == INICIO -> "DoggoApp"
            ruta == AGENDA -> "Mi Agenda"
            ruta == MENSAJES -> "Mensajes"
            ruta == PERFIL -> "Mi Perfil"
            else -> "DoggoApp"
        }
    }
}