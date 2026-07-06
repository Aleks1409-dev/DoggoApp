package com.piero.doggoapp.data.repository

import android.util.Log
import com.piero.doggoapp.data.remote.ApiService
import com.piero.doggoapp.domain.model.Cuidador

class CuidadorRepository(private val apiService: ApiService) {
    suspend fun obtenerCuidadores(): List<Cuidador> {
        val respuesta = apiService.getCuidadores()
        Log.d("DEBUG_PIERO", "ESTRUCTURA REAL: $respuesta")
        return emptyList()
    }
}