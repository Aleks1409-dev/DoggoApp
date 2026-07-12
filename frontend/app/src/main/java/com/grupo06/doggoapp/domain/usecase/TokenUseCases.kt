package com.grupo06.doggoapp.domain.usecase

data class TokenUseCases(
    val login: LoginUseCase,
    val register: RegisterUseCase,
    val logout: LogoutUseCase
)
