package com.grupo06.doggoapp.di

import com.grupo06.doggoapp.domain.usecase.CuidadorUseCases
import com.grupo06.doggoapp.domain.usecase.GetCuidadoresUseCase
import com.grupo06.doggoapp.domain.usecase.LoginUseCase
import com.grupo06.doggoapp.domain.usecase.LogoutUseCase
import com.grupo06.doggoapp.domain.usecase.RegisterUseCase
import com.grupo06.doggoapp.domain.usecase.TokenUseCases

class UseCaseModule(private val repositoryModule: RepositoryModule) {

    val tokenUseCases: TokenUseCases by lazy {
        TokenUseCases(
            login = LoginUseCase(repositoryModule.tokenRepository),
            register = RegisterUseCase(repositoryModule.tokenRepository),
            logout = LogoutUseCase(repositoryModule.tokenRepository)
        )
    }

    val cuidadorUseCases: CuidadorUseCases by lazy {
        CuidadorUseCases(getCuidadores = GetCuidadoresUseCase(repositoryModule.cuidadorRepository))
    }
}
