package com.piero.doggoapp.presentation.screens.inicio

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piero.doggoapp.data.repository.CuidadorRepository
import com.piero.doggoapp.domain.model.Cuidador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InicioViewModel(private val repository: CuidadorRepository) : ViewModel() {
    private val _cuidadores = MutableStateFlow<List<Cuidador>>(emptyList())
    val cuidadores = _cuidadores.asStateFlow()

    init {
        Log.d("DEBUG_PIERO", "InicioViewModel creado, llamando a cargarCuidadores")
        cargarCuidadores()
    }

    private fun cargarCuidadores() {
        viewModelScope.launch {
            try {
                val data = repository.obtenerCuidadores()
                Log.d("DEBUG_PIERO", "Datos recibidos: ${data.size}")
                _cuidadores.value = data
            } catch (e: Exception) {
                Log.e("DEBUG_PIERO", "El error es: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }
}