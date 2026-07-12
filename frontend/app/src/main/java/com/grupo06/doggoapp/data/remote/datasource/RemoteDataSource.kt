package com.grupo06.doggoapp.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.grupo06.doggoapp.core.network.ApiService
import com.grupo06.doggoapp.data.remote.dto.CreateAppointmentRequestDto
import com.grupo06.doggoapp.data.remote.dto.CreateAppointmentResponseDto
import com.grupo06.doggoapp.data.remote.dto.ErrorResponseDto
import com.grupo06.doggoapp.data.remote.dto.LoginRequestDto
import com.grupo06.doggoapp.data.remote.dto.LoginResponseDto
import com.grupo06.doggoapp.data.remote.dto.RegisterRequestDto
import com.grupo06.doggoapp.data.remote.dto.RegisterResponseDto
import com.grupo06.doggoapp.data.remote.dto.ScheduleResponseDto
import com.grupo06.doggoapp.data.remote.dto.ServiceDto
import com.grupo06.doggoapp.data.remote.dto.SittersResponseDto

class RemoteDataSource(private val apiService: ApiService) {

    suspend fun login(request: LoginRequestDto): LoginResponseDto {
        val response = apiService.login(request)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }

        throw Exception(parseError(response.errorBody()?.string()))
    }

    suspend fun register(request: RegisterRequestDto): RegisterResponseDto {
        val response = apiService.register(request)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta vacía del servidor")
        }

        throw Exception(parseError(response.errorBody()?.string()))
    }

    suspend fun getSitters(): SittersResponseDto? {
        val response = apiService.getSitters()
        if (response.isSuccessful) {
            return response.body()
        }
        throw Exception(parseError(response.errorBody()?.string()))
    }

    suspend fun getServices(): List<ServiceDto> {
        val response = apiService.getServices()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        }
        throw Exception(parseError(response.errorBody()?.string()))
    }

    suspend fun getSchedule(sitterId: String): ScheduleResponseDto? {
        val response = apiService.getSchedule(sitterId)
        if (response.isSuccessful) {
            return response.body()
        }
        throw Exception(parseError(response.errorBody()?.string()))
    }

    suspend fun createAppointment(
        sitterId: String,
        request: CreateAppointmentRequestDto
    ): CreateAppointmentResponseDto? {
        val response = apiService.createAppointment(sitterId, request)
        if (response.isSuccessful) {
            return response.body()
        }
        throw Exception(parseError(response.errorBody()?.string()))
    }

    private fun parseError(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) return "Ocurrió un error inesperado"
        return try {
            Gson().fromJson(errorBody, ErrorResponseDto::class.java).error
        } catch (e: Exception) {
            Log.e("RemoteDataSource", "Error parseando errorBody: $errorBody", e)
            "Ocurrió un error inesperado"
        }
    }
}
