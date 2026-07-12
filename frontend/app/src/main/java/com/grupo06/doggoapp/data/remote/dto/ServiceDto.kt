package com.grupo06.doggoapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.grupo06.doggoapp.domain.model.Servicio

/**
 * Representación cruda de un servicio ofrecido por un cuidador (GET /services).
 */
data class ServiceDto(
    val id: String?,
    val title: String?,
    val price: Double?,
    @SerializedName("sitter_id") val sitterId: String?,
    @SerializedName("created_at") val createdAt: String?
)

/**
 * Convierte el DTO en el modelo de dominio [Servicio].
 * Se descartan registros incompletos para evitar errores al calcular tarifas.
 */
fun ServiceDto.toDomain(): Servicio? {
    val safeId = id?.takeIf { it.isNotBlank() } ?: return null
    val safeTitle = title?.takeIf { it.isNotBlank() } ?: return null
    val safePrice = price ?: return null
    val safeSitterId = sitterId?.takeIf { it.isNotBlank() } ?: return null

    return Servicio(
        id = safeId,
        titulo = safeTitle,
        precio = safePrice,
        cuidadorId = safeSitterId,
        createdAt = createdAt
    )
}
