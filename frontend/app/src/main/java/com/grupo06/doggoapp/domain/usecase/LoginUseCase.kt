package com.grupo06.doggoapp.domain.usecase

import com.grupo06.doggoapp.domain.model.Token
import com.grupo06.doggoapp.domain.repository.TokenRepository

class LoginUseCase(private val repository: TokenRepository) {
    suspend operator fun invoke(email: String, password: String): Token {
        return repository.login(email, password)
    }
}
