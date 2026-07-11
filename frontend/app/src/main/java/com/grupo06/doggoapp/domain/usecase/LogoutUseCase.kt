package com.grupo06.doggoapp.domain.usecase

import com.grupo06.doggoapp.domain.repository.TokenRepository

class LogoutUseCase(private val repository: TokenRepository) {
    operator fun invoke() = repository.logout()
}
