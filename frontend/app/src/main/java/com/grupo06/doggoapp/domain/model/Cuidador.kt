package com.grupo06.doggoapp.domain.model

/**
 * Servicio ofrecido por un cuidador. Proviene de GET /services.
 */
data class Servicio(
    val id: String,
    val titulo: String,
    val precio: Double,
    val cuidadorId: String,
    val createdAt: String? = null
)

/**
 * Modelo de dominio de un cuidador.
 *
 * El backend actual solo devuelve [id], [nombre], [email], [role] y [createdAt];
 * el resto de campos (ubicación, rating, foto, especialidades, etc.) se completa
 * temporalmente con datos locales de demostración.
 */
data class Cuidador(
    val id: String,
    val nombre: String,
    val email: String = "",
    val ubicacion: String = "Ubicación no registrada",
    val rating: Double = 0.0,
    val tipo: String = "Servicios generales",
    val experiencia: String = "Experiencia no registrada",
    val especialidades: List<String> = emptyList(),
    val fotoResId: Int? = null,
    val servicios: List<Servicio> = emptyList(),
    val tarifaMinima: Double? = null,
    val disponible: Boolean = true,
    val premium: Boolean = false,
    val createdAt: String? = null
)
