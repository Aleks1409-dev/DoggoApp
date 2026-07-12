package com.grupo06.doggoapp.domain.usecase

import com.grupo06.doggoapp.data.repository.CuidadoresResultado
import com.grupo06.doggoapp.domain.repository.CuidadorRepository
import kotlinx.coroutines.flow.Flow

class GetCuidadoresFlowUseCase(private val repository: CuidadorRepository) {
    operator fun invoke(): Flow<CuidadoresResultado> = repository.obtenerCuidadores()
}
