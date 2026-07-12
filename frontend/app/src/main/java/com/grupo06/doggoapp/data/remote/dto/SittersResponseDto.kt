package com.grupo06.doggoapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Cuerpo de respuesta de GET /sitters.
 */
data class SittersResponseDto(
    @SerializedName("sitters") val sitters: List<SitterDto>?
)
