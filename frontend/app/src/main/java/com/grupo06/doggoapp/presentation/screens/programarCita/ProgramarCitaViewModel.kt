package com.grupo06.doggoapp.presentation.screens.programarCita

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo06.doggoapp.data.repository.AgendaRepository
import com.grupo06.doggoapp.domain.repository.CuidadorRepository
import com.grupo06.doggoapp.data.repository.CuidadorResultado
import com.grupo06.doggoapp.data.repository.ResultadoAgendar
import com.grupo06.doggoapp.data.repository.ResultadoDisponibilidad
import com.grupo06.doggoapp.domain.model.Servicio
import com.grupo06.doggoapp.domain.model.Slot
import com.grupo06.doggoapp.presentation.events.EventBus
import com.grupo06.doggoapp.presentation.events.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProgramarCitaViewModel(
    private val sitterId: String,
    private val agendaRepository: AgendaRepository,
    private val cuidadorRepository: CuidadorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgramarCitaUiState())
    val uiState = _uiState.asStateFlow()

    private var slotsDisponibles: List<Slot> = emptyList()
    private var serviciosDelCuidador: List<Servicio> = emptyList()

    init {
        cargarDatos()
    }

    fun seleccionarServicio(servicio: Servicio?) {
        _uiState.update { it.copy(servicioSeleccionado = servicio) }
    }

    fun seleccionarFecha(fecha: String?) {
        _uiState.update { it.copy(fechaSeleccionada = fecha, rangoSeleccionado = null) }
    }

    fun seleccionarRango(rango: String?) {
        _uiState.update { it.copy(rangoSeleccionado = rango) }
    }

    fun reintentar() {
        cargarDatos()
    }

    fun confirmarCita() {
        val state = _uiState.value
        val fecha = state.fechaSeleccionada ?: return
        val rango = state.rangoSeleccionado ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                agendaRepository.agendarCita(sitterId, fecha, rango).collect { resultado ->
                    when (resultado) {
                        is ResultadoAgendar.Cargando -> {
                            _uiState.update { it.copy(estado = ProgramarCitaEstado.Confirmando) }
                        }

                        is ResultadoAgendar.Exito -> {
                            EventBus.send(UiEvent.Success(resultado.mensaje))
                            _uiState.update {
                                it.copy(estado = ProgramarCitaEstado.Exito(resultado.mensaje))
                            }
                        }

                        is ResultadoAgendar.Error -> {
                            Log.e("ProgramarCitaVM", "Error agendando cita: ${resultado.mensaje}")
                            EventBus.send(UiEvent.Error(resultado.mensaje))
                            _uiState.update {
                                it.copy(
                                    estado = ProgramarCitaEstado.DisponibilidadCargada(
                                        slots = slotsDisponibles,
                                        serviciosDelCuidador = serviciosDelCuidador
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ProgramarCitaVM", "Error inesperado agendando cita: ${e.message}", e)
                EventBus.send(UiEvent.Error("Error inesperado al agendar la cita"))
                _uiState.update {
                    it.copy(
                        estado = ProgramarCitaEstado.DisponibilidadCargada(
                            slots = slotsDisponibles,
                            serviciosDelCuidador = serviciosDelCuidador
                        )
                    )
                }
            }
        }
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            _uiState.update { it.copy(estado = ProgramarCitaEstado.Loading) }

            try {
                val disponibilidadDeferred = async(Dispatchers.IO) {
                    try {
                        agendaRepository.obtenerDisponibilidad(sitterId)
                            .first { it !is ResultadoDisponibilidad.Cargando }
                    } catch (e: Exception) {
                        Log.e("ProgramarCitaVM", "Error en async disponibilidad: ${e.message}", e)
                        ResultadoDisponibilidad.Error(
                            e.localizedMessage ?: "Error al consultar disponibilidad"
                        )
                    }
                }
                val cuidadorDeferred = async(Dispatchers.IO) {
                    try {
                        cuidadorRepository.obtenerCuidador(sitterId)
                            .first { it !is CuidadorResultado.Cargando }
                    } catch (e: Exception) {
                        Log.e("ProgramarCitaVM", "Error en async cuidador: ${e.message}", e)
                        CuidadorResultado.Error(
                            Exception(e.localizedMessage ?: "Error al cargar cuidador")
                        )
                    }
                }

                val disponibilidad = disponibilidadDeferred.await()
                val cuidadorResultado = cuidadorDeferred.await()

                // Siempre intentamos capturar los datos del cuidador si están disponibles
                if (cuidadorResultado is CuidadorResultado.Exito) {
                    serviciosDelCuidador = cuidadorResultado.cuidador.servicios
                    _uiState.update { it.copy(nombreCuidador = cuidadorResultado.cuidador.nombre) }
                }

                when {
                    disponibilidad is ResultadoDisponibilidad.Exito &&
                            cuidadorResultado is CuidadorResultado.Exito -> {
                        slotsDisponibles = disponibilidad.slots
                        _uiState.update {
                            it.copy(
                                estado = ProgramarCitaEstado.DisponibilidadCargada(
                                    slots = slotsDisponibles,
                                    serviciosDelCuidador = serviciosDelCuidador
                                )
                            )
                        }
                    }

                    disponibilidad is ResultadoDisponibilidad.Error -> {
                        _uiState.update {
                            it.copy(estado = ProgramarCitaEstado.Error(disponibilidad.mensaje))
                        }
                    }

                    cuidadorResultado is CuidadorResultado.Error -> {
                        _uiState.update {
                            it.copy(
                                estado = ProgramarCitaEstado.Error(
                                    cuidadorResultado.exception.localizedMessage
                                        ?: "Error al cargar el cuidador"
                                )
                            )
                        }
                    }

                    else -> {
                        _uiState.update {
                            it.copy(estado = ProgramarCitaEstado.Error("No se pudieron cargar los datos"))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ProgramarCitaVM", "Error cargando datos: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        estado = ProgramarCitaEstado.Error(
                            e.localizedMessage ?: "Error inesperado al cargar la pantalla"
                        )
                    )
                }
            }
        }
    }
}
