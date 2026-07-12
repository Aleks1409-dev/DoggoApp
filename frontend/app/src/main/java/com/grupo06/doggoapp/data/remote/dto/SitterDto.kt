package com.grupo06.doggoapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.grupo06.doggoapp.domain.model.Cuidador

/**
 * Representación cruda de un cuidador (sitter) devuelta por GET /sitters.
 */
data class SitterDto(
    val id: String?,
    val name: String?,
    val email: String?,
    val role: String?,
    @SerializedName("created_at") val createdAt: String?
)

/**
 * Convierte el DTO en un modelo de dominio base.
 * Los datos de demostración (foto, ubicación, especialidades, etc.) se agregan
 * posteriormente en el repositorio mediante [com.grupo06.doggoapp.data.local.DemoCaregiverData].
 */
fun SitterDto.toDomain(): Cuidador? {
    val safeId = id?.takeIf { it.isNotBlank() } ?: return null
    val safeName = name?.takeIf { it.isNotBlank() } ?: return null

    return Cuidador(
        id = safeId,
        nombre = safeName,
        email = email.orEmpty(),
        createdAt = createdAt
    )
}
