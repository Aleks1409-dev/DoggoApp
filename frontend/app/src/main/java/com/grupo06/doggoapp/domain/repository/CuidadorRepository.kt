package com.grupo06.doggoapp.domain.repository

import com.grupo06.doggoapp.domain.model.Cuidador
import com.grupo06.doggoapp.data.repository.CuidadoresResultado
import com.grupo06.doggoapp.data.repository.CuidadorResultado
import kotlinx.coroutines.flow.Flow

interface CuidadorRepository {
    /**
     * Versión suspendida simple usada por los casos de uso heredados.
     */
    suspend fun getCuidadores(): List<Cuidador>

    /**
     * Flujo con estados de carga, éxito, vacío y error.
     */
    fun obtenerCuidadores(): Flow<CuidadoresResultado>

    /**
     * Recupera un cuidador por su [sitterId].
     */
    fun obtenerCuidador(sitterId: String): Flow<CuidadorResultado>
}
