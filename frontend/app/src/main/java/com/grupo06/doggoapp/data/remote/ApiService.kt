package com.grupo06.doggoapp.data.remote

import com.grupo06.doggoapp.data.remote.dto.CreateAppointmentRequestDto
import com.grupo06.doggoapp.data.remote.dto.CreateAppointmentResponseDto
import com.grupo06.doggoapp.data.remote.dto.ScheduleResponseDto
import com.grupo06.doggoapp.data.remote.dto.ServiceDto
import com.grupo06.doggoapp.data.remote.dto.SittersResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Contrato de los endpoints disponibles del backend DoggoApp.
 *
 * NOTA: Los endpoints antiguos de `CuidadoresApi` fueron eliminados porque no existen
 * en el backend real. Ahora se consumen `/sitters` y `/services`.
 */
interface ApiService {

    @GET("sitters")
    suspend fun getSitters(): SittersResponseDto

    @GET("services")
    suspend fun getServices(): List<ServiceDto>

    @GET("schedule/{sitterId}")
    suspend fun getSchedule(@Path("sitterId") sitterId: String): ScheduleResponseDto

    @POST("schedule/{sitterId}")
    suspend fun createAppointment(
        @Path("sitterId") sitterId: String,
        @Body request: CreateAppointmentRequestDto
    ): CreateAppointmentResponseDto
}
