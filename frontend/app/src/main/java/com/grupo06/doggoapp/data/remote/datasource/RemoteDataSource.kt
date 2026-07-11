package com.grupo06.doggoapp.data.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.grupo06.doggoapp.core.network.ApiService
import com.grupo06.doggoapp.data.remote.dto.ErrorResponseDto
import com.grupo06.doggoapp.data.remote.dto.LoginRequestDto
import com.grupo06.doggoapp.data.remote.dto.LoginResponseDto
import com.grupo06.doggoapp.data.remote.dto.RegisterRequestDto
import com.grupo06.doggoapp.data.remote.dto.RegisterResponseDto

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

    suspend fun getCuidadores(): Map<String, Any>? {
        val response = apiService.getCuidadores()
        return response.body()
    }

    private fun parseError(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) return "Ocurrió un error inesperado"
        return try {
            Gson().fromJson(errorBody, ErrorResponseDto::class.java).error
        } catch (e: Exception) {
            "Ocurrió un error inesperado"
        }
    }
}
