package com.grupo06.doggoapp.data.repository

import android.util.Log
import com.grupo06.doggoapp.data.remote.ApiService
import com.grupo06.doggoapp.domain.model.Cuidador

class CuidadorRepository(private val apiService: ApiService) {
    suspend fun obtenerCuidadores(): List<Cuidador> {
        val respuesta = apiService.getCuidadores()
        Log.d("DEBUG_PIERO", "ESTRUCTURA REAL: $respuesta")
        return emptyList()
    }
}