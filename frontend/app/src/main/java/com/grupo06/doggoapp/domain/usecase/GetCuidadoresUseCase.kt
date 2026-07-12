package com.grupo06.doggoapp.domain.usecase

import com.grupo06.doggoapp.domain.repository.CuidadorRepository

class GetCuidadoresUseCase(private val repository: CuidadorRepository) {
    suspend operator fun invoke() = repository.getCuidadores()
}
