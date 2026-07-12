package com.grupo06.doggoapp.data.repository

import android.util.Log
import com.grupo06.doggoapp.data.local.DemoCaregiverData
import com.grupo06.doggoapp.data.remote.ApiService
import com.grupo06.doggoapp.data.remote.dto.toDomain
import com.grupo06.doggoapp.domain.model.Cuidador
import com.grupo06.doggoapp.domain.model.Servicio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * Estados posibles del listado de cuidadores.
 */
sealed interface CuidadoresResultado {
    data object Cargando : CuidadoresResultado
    data class Exito(
        val cuidadores: List<Cuidador>,
        val serviciosDisponibles: List<String>
    ) : CuidadoresResultado

    data object Vacio : CuidadoresResultado
    data class Error(val exception: Throwable) : CuidadoresResultado
}

/**
 * Estados posibles de la carga de un cuidador individual.
 */
sealed interface CuidadorResultado {
    data object Cargando : CuidadorResultado
    data class Exito(val cuidador: Cuidador) : CuidadorResultado
    data class Error(val exception: Throwable) : CuidadorResultado
}

class CuidadorRepository(private val apiService: ApiService) {

    /**
     * Obtiene los cuidadores y sus servicios asociados.
     *
     * Combina GET /sitters con GET /services, calcula la tarifa mínima por cuidador
     * y enriquece los datos con información local de demostración mientras el backend
     * no provea ubicación, especialidades, foto, etc.
     */
    fun obtenerCuidadores(): Flow<CuidadoresResultado> = flow {
        emit(CuidadoresResultado.Cargando)

        try {
            val sittersResponse = withContext(Dispatchers.IO) { apiService.getSitters() }
            val servicesDto = withContext(Dispatchers.IO) { apiService.getServices() }

            val servicios = servicesDto.mapNotNull { it.toDomain() }
            val cuidadores = sittersResponse.sitters.orEmpty().mapNotNull { dto ->
                val base = dto.toDomain() ?: return@mapNotNull null
                val serviciosDelCuidador = servicios.filter { it.cuidadorId == base.id }
                val tarifaMinima = serviciosDelCuidador.minOfOrNull { it.precio }
                val demo = DemoCaregiverData.getFor(
                    email = base.email,
                    nombre = base.nombre
                )

                base.copy(
                    ubicacion = demo.ubicacion,
                    experiencia = demo.experiencia,
                    especialidades = demo.especialidades,
                    fotoResId = demo.fotoResId,
                    rating = demo.rating,
                    tipo = demo.tipo,
                    servicios = serviciosDelCuidador,
                    tarifaMinima = tarifaMinima,
                    disponible = demo.disponible,
                    premium = demo.premium
                )
            }

            Log.d("CuidadorRepository", "Cuidadores cargados: ${cuidadores.size}")

            val serviciosDisponibles = servicios
                .map { it.titulo }
                .distinct()
                .sorted()

            if (cuidadores.isEmpty()) {
                emit(CuidadoresResultado.Vacio)
            } else {
                emit(CuidadoresResultado.Exito(cuidadores, serviciosDisponibles))
            }
        } catch (e: Exception) {
            Log.e("CuidadorRepository", "Error al cargar cuidadores: ${e.message}", e)
            emit(CuidadoresResultado.Error(e))
        }
    }

    /**
     * Recupera un cuidador por su [sitterId] reutilizando el flujo de [obtenerCuidadores].
     *
     * Útil mientras el backend no exponga un endpoint individual como GET /sitters/{id}.
     */
    fun obtenerCuidador(sitterId: String): Flow<CuidadorResultado> = flow {
        emit(CuidadorResultado.Cargando)

        obtenerCuidadores().collect { resultado ->
            when (resultado) {
                is CuidadoresResultado.Cargando -> { /* ya emitido */ }
                is CuidadoresResultado.Exito -> {
                    val cuidador = resultado.cuidadores.find { it.id == sitterId }
                    if (cuidador != null) {
                        emit(CuidadorResultado.Exito(cuidador))
                    } else {
                        emit(
                            CuidadorResultado.Error(
                                NoSuchElementException("No se encontró el cuidador solicitado")
                            )
                        )
                    }
                }
                is CuidadoresResultado.Vacio -> {
                    emit(
                        CuidadorResultado.Error(
                            NoSuchElementException("No se encontró el cuidador solicitado")
                        )
                    )
                }
                is CuidadoresResultado.Error -> {
                    emit(CuidadorResultado.Error(resultado.exception))
                }
            }
        }
    }
}
