package com.grupo06.doggoapp.data.repository

import android.util.Log
import com.grupo06.doggoapp.core.network.ApiService
import com.grupo06.doggoapp.core.session.SessionManager
import com.grupo06.doggoapp.data.remote.dto.CreateAppointmentRequestDto
import com.grupo06.doggoapp.domain.model.Slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * Estados posibles de la consulta de disponibilidad de un cuidador.
 */
sealed interface ResultadoDisponibilidad {
    data object Cargando : ResultadoDisponibilidad
    data class Exito(val slots: List<Slot>) : ResultadoDisponibilidad
    data class Error(val mensaje: String) : ResultadoDisponibilidad
}

/**
 * Estados posibles del agendamiento de una cita.
 */
sealed interface ResultadoAgendar {
    data object Cargando : ResultadoAgendar
    data class Exito(val mensaje: String, val fecha: String, val rango: String) : ResultadoAgendar
    data class Error(val mensaje: String) : ResultadoAgendar
}

class AgendaRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    /**
     * Obtiene los slots disponibles de un cuidador.
     *
     * Devuelve [ResultadoDisponibilidad.Exito] con una lista vacía si el cuidador
     * no tiene agenda registrada o no tiene días disponibles.
     */
    fun obtenerDisponibilidad(sitterId: String): Flow<ResultadoDisponibilidad> = flow {
        emit(ResultadoDisponibilidad.Cargando)

        try {
            val response = withContext(Dispatchers.IO) { apiService.getSchedule(sitterId) }
            if (response.isSuccessful) {
                val slots = response.body()?.daysAvailable.orEmpty().mapNotNull { dto ->
                    val fecha = dto.appointmentDate?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                    val rango = dto.appointmentRange?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                    Slot(fecha = fecha, rango = rango)
                }
                emit(ResultadoDisponibilidad.Exito(slots))
            } else {
                val mensaje = extraerMensajeError(response.errorBody()?.string())
                    ?: "Error al consultar la disponibilidad"
                emit(ResultadoDisponibilidad.Error(mensaje))
            }
        } catch (e: HttpException) {
            val mensaje = extraerMensajeError(e.response()?.errorBody()?.string())
                ?: "Error al consultar la disponibilidad"
            Log.e("AgendaRepository", "HTTP ${e.code()} obteniendo agenda: $mensaje", e)
            emit(ResultadoDisponibilidad.Error(mensaje))
        } catch (e: Exception) {
            Log.e("AgendaRepository", "Error de red obteniendo agenda: ${e.message}", e)
            emit(ResultadoDisponibilidad.Error("Error de red. Verifica tu conexión e inténtalo de nuevo."))
        }
    }

    /**
     * Crea una cita con el cuidador en el slot seleccionado.
     *
     * El [client_id] proviene del email almacenado en [SessionManager] tras el login.
     */
    fun agendarCita(
        sitterId: String,
        fecha: String,
        rango: String
    ): Flow<ResultadoAgendar> = flow {
        emit(ResultadoAgendar.Cargando)

        try {
            val clientId = sessionManager.obtenerEmail()
                ?: throw IllegalStateException("No hay sesión activa. Inicia sesión para reservar.")

            val request = CreateAppointmentRequestDto(
                clientId = clientId,
                appointmentDate = fecha,
                appointmentRange = rango
            )
            val response = withContext(Dispatchers.IO) { apiService.createAppointment(sitterId, request) }
            if (response.isSuccessful) {
                val body = response.body()
                val mensaje = body?.message?.takeIf { it.isNotBlank() }
                    ?: "Cita agendada correctamente"
                emit(
                    ResultadoAgendar.Exito(
                        mensaje = mensaje,
                        fecha = body?.appointmentDate ?: fecha,
                        rango = body?.appointmentRange ?: rango
                    )
                )
            } else {
                val mensaje = extraerMensajeError(response.errorBody()?.string())
                    ?: "El día o el rango solicitado no está disponible"
                emit(ResultadoAgendar.Error(mensaje))
            }
        } catch (e: HttpException) {
            val mensaje = extraerMensajeError(e.response()?.errorBody()?.string())
                ?: "El día o el rango solicitado no está disponible"
            Log.e("AgendaRepository", "HTTP ${e.code()} agendando cita: $mensaje", e)
            emit(ResultadoAgendar.Error(mensaje))
        } catch (e: Exception) {
            Log.e("AgendaRepository", "Error de red agendando cita: ${e.message}", e)
            emit(ResultadoAgendar.Error("Error de red. Verifica tu conexión e inténtalo de nuevo."))
        }
    }

    private fun extraerMensajeError(json: String?): String? {
        return try {
            json?.let {
                val regex = """"error"\s*:\s*"([^"]+)"""".toRegex()
                regex.find(it)?.groupValues?.get(1)
            }
        } catch (_: Exception) {
            null
        }
    }
}
