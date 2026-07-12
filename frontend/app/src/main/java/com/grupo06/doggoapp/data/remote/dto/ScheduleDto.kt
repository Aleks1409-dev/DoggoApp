package com.grupo06.doggoapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Representa un slot de disponibilidad devuelto por GET /schedule/{sitterId}.
 */
data class SlotDto(
    @SerializedName("appointment_date") val appointmentDate: String?,
    @SerializedName("appointment_range") val appointmentRange: String?
)

/**
 * Respuesta de GET /schedule/{sitterId}.
 */
data class ScheduleResponseDto(
    @SerializedName("days_available") val daysAvailable: List<SlotDto>?
)

/**
 * Cuerpo de la petición POST /schedule/{sitterId}.
 */
data class CreateAppointmentRequestDto(
    @SerializedName("client_id") val clientId: String,
    @SerializedName("appointment_date") val appointmentDate: String,
    @SerializedName("appointment_range") val appointmentRange: String
)

/**
 * Respuesta de POST /schedule/{sitterId}.
 */
data class CreateAppointmentResponseDto(
    @SerializedName("message") val message: String?,
    @SerializedName("appointment_date") val appointmentDate: String?,
    @SerializedName("appointment_range") val appointmentRange: String?
)
