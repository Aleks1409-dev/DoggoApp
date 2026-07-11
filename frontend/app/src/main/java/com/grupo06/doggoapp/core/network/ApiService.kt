package com.grupo06.doggoapp.core.network

import com.grupo06.doggoapp.data.remote.dto.LoginRequestDto
import com.grupo06.doggoapp.data.remote.dto.LoginResponseDto
import com.grupo06.doggoapp.data.remote.dto.RegisterRequestDto
import com.grupo06.doggoapp.data.remote.dto.RegisterResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @POST("register")
    suspend fun register(@Body request: RegisterRequestDto): Response<RegisterResponseDto>

    @GET("sitters")
    suspend fun getCuidadores(): Response<Map<String, Any>>
}
