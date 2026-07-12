package com.grupo06.doggoapp.data.repository

import android.util.Log
import com.grupo06.doggoapp.data.remote.ApiService
import com.grupo06.doggoapp.data.remote.dto.CreateAppointmentRequestDto
import com.grupo06.doggoapp.domain.model.SessionProvider
import com.grupo06.doggoapp.domain.model.Slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.flowOn
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

class AgendaRepository(private val apiService: ApiService) {

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
            val slots = response.daysAvailable.orEmpty().mapNotNull { dto ->
                val fecha = dto.appointmentDate?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val rango = dto.appointmentRange?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                Slot(fecha = fecha, rango = rango)
            }
            emit(ResultadoDisponibilidad.Exito(slots))
        } catch (e: HttpException) {
            val mensaje = extraerMensajeError(e) ?: "Error al consultar la disponibilidad"
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
     * El [client_id] proviene de [SessionProvider] hasta que se implemente la
     * autenticación real en HU005.
     */
    fun agendarCita(
        sitterId: String,
        fecha: String,
        rango: String
    ): Flow<ResultadoAgendar> = flow {
        emit(ResultadoAgendar.Cargando)

        try {
            val request = CreateAppointmentRequestDto(
                clientId = SessionProvider.clienteId,
                appointmentDate = fecha,
                appointmentRange = rango
            )
            val response = withContext(Dispatchers.IO) { apiService.createAppointment(sitterId, request) }
            val mensaje = response.message?.takeIf { it.isNotBlank() }
                ?: "Cita agendada correctamente"
            emit(
                ResultadoAgendar.Exito(
                    mensaje = mensaje,
                    fecha = response.appointmentDate ?: fecha,
                    rango = response.appointmentRange ?: rango
                )
            )
        } catch (e: HttpException) {
            val mensaje = extraerMensajeError(e)
                ?: "El día o el rango solicitado no está disponible"
            Log.e("AgendaRepository", "HTTP ${e.code()} agendando cita: $mensaje", e)
            emit(ResultadoAgendar.Error(mensaje))
        } catch (e: Exception) {
            Log.e("AgendaRepository", "Error de red agendando cita: ${e.message}", e)
            emit(ResultadoAgendar.Error("Error de red. Verifica tu conexión e inténtalo de nuevo."))
        }
    }

    private fun extraerMensajeError(e: HttpException): String? {
        return try {
            e.response()?.errorBody()?.string()?.let { json ->
                // Extrae el valor del campo "error" si existe.
                val regex = """"error"\s*:\s*"([^"]+)"""".toRegex()
                regex.find(json)?.groupValues?.get(1)
            }
        } catch (_: Exception) {
            null
        }
    }
}
