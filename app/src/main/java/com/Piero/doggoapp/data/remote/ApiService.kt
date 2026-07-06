package com.piero.doggoapp.data.remote

import com.piero.doggoapp.domain.model.ApiData
import com.piero.doggoapp.domain.model.Cuidador
import retrofit2.http.*

interface ApiService {
    @GET("CuidadoresApi")
    suspend fun getCuidadores(): Map<String, Any>

    @GET("CuidadoresApi/{id}")
    suspend fun getCuidadorById(@Path("id") id: String): Cuidador

    @POST("CuidadoresApi")
    suspend fun addCuidador(@Body cuidador: Cuidador): Cuidador

    @PUT("CuidadoresApi/{id}")
    suspend fun updateCuidador(@Path("id") id: String, @Body cuidador: Cuidador): Cuidador

    @DELETE("CuidadoresApi/{id}")
    suspend fun deleteCuidador(@Path("id") id: String)
}