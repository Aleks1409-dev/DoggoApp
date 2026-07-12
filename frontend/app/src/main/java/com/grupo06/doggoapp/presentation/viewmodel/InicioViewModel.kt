package com.grupo06.doggoapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo06.doggoapp.domain.usecase.CuidadorUseCases
import com.grupo06.doggoapp.presentation.event.EventBus
import com.grupo06.doggoapp.presentation.event.UiEvent
import com.grupo06.doggoapp.presentation.screens.inicio.InicioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InicioViewModel(private val useCases: CuidadorUseCases) : ViewModel() {

    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState = _uiState.asStateFlow()

    init {
        cargarCuidadores()
    }

    fun cargarCuidadores() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val cuidadores = useCases.getCuidadores()

                Log.d("API_TEST", "Éxito: Se recibieron ${cuidadores.size} cuidadores de AWS")

                _uiState.value = _uiState.value.copy(cuidadores = cuidadores, isLoading = false)
            } catch (e: Exception) {
                Log.e("API_TEST", "Error al conectar con AWS: ${e.message}")

                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                EventBus.send(UiEvent.Error("Error al listar cuidadores: ${e.message}"))
            }
        }
    }
}