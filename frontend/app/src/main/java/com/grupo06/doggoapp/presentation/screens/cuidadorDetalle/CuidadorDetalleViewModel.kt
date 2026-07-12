package com.grupo06.doggoapp.presentation.screens.cuidadorDetalle

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo06.doggoapp.data.repository.CuidadorRepository
import com.grupo06.doggoapp.data.repository.CuidadorResultado
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CuidadorDetalleViewModel(
    private val sitterId: String,
    private val repository: CuidadorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CuidadorDetalleUiState())
    val uiState = _uiState.asStateFlow()

    private var cargaJob: Job? = null

    init {
        cargarCuidador()
    }

    fun reintentar() {
        cargarCuidador()
    }

    fun toggleFavorito() {
        _uiState.update { it.copy(esFavorito = !it.esFavorito) }
    }

    private fun cargarCuidador() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch {
            repository.obtenerCuidador(sitterId).collect { resultado ->
                when (resultado) {
                    is CuidadorResultado.Cargando -> {
                        _uiState.update { it.copy(estado = CuidadorDetalleEstado.Loading) }
                    }

                    is CuidadorResultado.Exito -> {
                        _uiState.update {
                            it.copy(estado = CuidadorDetalleEstado.Success(resultado.cuidador))
                        }
                    }

                    is CuidadorResultado.Error -> {
                        Log.e(
                            "CuidadorDetalleVM",
                            "Error cargando cuidador",
                            resultado.exception
                        )
                        _uiState.update {
                            it.copy(
                                estado = CuidadorDetalleEstado.Error(
                                    resultado.exception.localizedMessage
                                        ?: "Ocurrió un error al cargar el perfil"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cargaJob?.cancel()
    }
}
