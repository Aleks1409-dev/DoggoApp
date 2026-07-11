package com.grupo06.doggoapp.domain.usecase

import com.grupo06.doggoapp.domain.repository.TokenRepository

class RegisterUseCase(private val repository: TokenRepository) {
    suspend operator fun invoke(
        names: String,
        surnames: String,
        email: String,
        password: String,
        termsAccepted: Boolean
    ) {
        repository.register(names, surnames, email, password, termsAccepted)
    }
}
