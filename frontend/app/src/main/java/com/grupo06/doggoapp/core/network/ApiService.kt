package com.grupo06.doggoapp.core.network

import com.grupo06.doggoapp.data.remote.dto.CreateAppointmentRequestDto
import com.grupo06.doggoapp.data.remote.dto.CreateAppointmentResponseDto
import com.grupo06.doggoapp.data.remote.dto.LoginRequestDto
import com.grupo06.doggoapp.data.remote.dto.LoginResponseDto
import com.grupo06.doggoapp.data.remote.dto.RegisterRequestDto
import com.grupo06.doggoapp.data.remote.dto.RegisterResponseDto
import com.grupo06.doggoapp.data.remote.dto.ScheduleResponseDto
import com.grupo06.doggoapp.data.remote.dto.ServiceDto
import com.grupo06.doggoapp.data.remote.dto.SittersResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @POST("register")
    suspend fun register(@Body request: RegisterRequestDto): Response<RegisterResponseDto>

    @GET("sitters")
    suspend fun getSitters(): Response<SittersResponseDto>

    @GET("services")
    suspend fun getServices(): Response<List<ServiceDto>>

    @GET("schedule/{sitterId}")
    suspend fun getSchedule(@Path("sitterId") sitterId: String): Response<ScheduleResponseDto>

    @POST("schedule/{sitterId}")
    suspend fun createAppointment(
        @Path("sitterId") sitterId: String,
        @Body request: CreateAppointmentRequestDto
    ): Response<CreateAppointmentResponseDto>
}
