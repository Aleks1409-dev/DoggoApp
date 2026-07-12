package com.grupo06.doggoapp.presentation.screens.inicio

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo06.doggoapp.data.repository.CuidadorRepository
import com.grupo06.doggoapp.data.repository.CuidadoresResultado
import com.grupo06.doggoapp.domain.model.Cuidador
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InicioViewModel(private val repository: CuidadorRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState = _uiState.asStateFlow()

    private var todosLosCuidadores: List<Cuidador> = emptyList()
    private var cargaJob: Job? = null

    init {
        cargarCuidadores()
    }

    fun reintentar() {
        cargarCuidadores()
    }

    fun buscar(texto: String) {
        _uiState.update { it.copy(filtros = it.filtros.copy(busqueda = texto)) }
        aplicarFiltros()
    }

    fun filtrarPorServicio(servicio: String?) {
        _uiState.update { it.copy(filtros = it.filtros.copy(servicioSeleccionado = servicio)) }
        aplicarFiltros()
    }

    fun ordenarPorPrecio(ascendente: Boolean) {
        _uiState.update { it.copy(filtros = it.filtros.copy(ordenAscendente = ascendente)) }
        aplicarFiltros()
    }

    private fun cargarCuidadores() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch {
            repository.obtenerCuidadores().collect { resultado ->
                when (resultado) {
                    is CuidadoresResultado.Cargando -> {
                        _uiState.update { it.copy(estado = InicioEstado.Loading) }
                    }

                    is CuidadoresResultado.Exito -> {
                        todosLosCuidadores = resultado.cuidadores
                        _uiState.update {
                            it.copy(serviciosDisponibles = resultado.serviciosDisponibles)
                        }
                        aplicarFiltros()
                    }

                    is CuidadoresResultado.Vacio -> {
                        todosLosCuidadores = emptyList()
                        _uiState.update { it.copy(estado = InicioEstado.Empty) }
                    }

                    is CuidadoresResultado.Error -> {
                        Log.e("InicioViewModel", "Error cargando cuidadores", resultado.exception)
                        _uiState.update {
                            it.copy(
                                estado = InicioEstado.Error(
                                    resultado.exception.localizedMessage
                                        ?: "Ocurrió un error al cargar los cuidadores"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun aplicarFiltros() {
        val filtros = _uiState.value.filtros
        val textoBusqueda = filtros.busqueda.trim().lowercase()

        val filtrados = todosLosCuidadores
            .filter { cuidador ->
                val coincideBusqueda = textoBusqueda.isEmpty() ||
                    cuidador.nombre.lowercase().contains(textoBusqueda) ||
                    cuidador.ubicacion.lowercase().contains(textoBusqueda) ||
                    cuidador.tipo.lowercase().contains(textoBusqueda) ||
                    cuidador.especialidades.any { it.lowercase().contains(textoBusqueda) }

                val coincideServicio = filtros.servicioSeleccionado == null ||
                    cuidador.servicios.any {
                        it.titulo.equals(filtros.servicioSeleccionado, ignoreCase = true)
                    } ||
                    cuidador.especialidades.any {
                        it.equals(filtros.servicioSeleccionado, ignoreCase = true)
                    }

                coincideBusqueda && coincideServicio
            }
            .sortedWith(compareBy { it.tarifaMinima ?: Double.MAX_VALUE })
            .let { lista ->
                if (filtros.ordenAscendente) lista else lista.reversed()
            }

        val nuevoEstado = if (filtrados.isEmpty()) {
            InicioEstado.Empty
        } else {
            InicioEstado.Success(filtrados)
        }

        _uiState.update { it.copy(estado = nuevoEstado) }
    }

    override fun onCleared() {
        super.onCleared()
        cargaJob?.cancel()
    }
}
