package com.grupo06.doggoapp.di

import com.grupo06.doggoapp.data.remote.datasource.RemoteDataSource
import com.grupo06.doggoapp.data.repository.AgendaRepository
import com.grupo06.doggoapp.data.repository.CuidadorRepositoryImpl
import com.grupo06.doggoapp.data.repository.TokenRepositoryImpl
import com.grupo06.doggoapp.domain.repository.CuidadorRepository
import com.grupo06.doggoapp.domain.repository.TokenRepository

class RepositoryModule(private val networkModule: NetworkModule) {

    private val remoteDataSource: RemoteDataSource by lazy {
        RemoteDataSource(networkModule.apiService)
    }

    val tokenRepository: TokenRepository by lazy {
        TokenRepositoryImpl(remoteDataSource, networkModule.sessionManager)
    }

    val cuidadorRepository: CuidadorRepository by lazy {
        CuidadorRepositoryImpl(remoteDataSource)
    }

    val agendaRepository: AgendaRepository by lazy {
        AgendaRepository(networkModule.apiService, networkModule.sessionManager)
    }
}
