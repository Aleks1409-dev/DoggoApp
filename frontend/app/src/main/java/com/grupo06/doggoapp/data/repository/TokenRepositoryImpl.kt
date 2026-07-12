package com.grupo06.doggoapp.data.repository

import com.grupo06.doggoapp.core.session.SessionManager
import com.grupo06.doggoapp.data.mapper.TokenMapper
import com.grupo06.doggoapp.data.remote.datasource.RemoteDataSource
import com.grupo06.doggoapp.domain.model.Token
import com.grupo06.doggoapp.domain.repository.TokenRepository

class TokenRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val sessionManager: SessionManager
) : TokenRepository {

    override suspend fun login(email: String, password: String): Token {
        val requestDto = TokenMapper.toRequest(email, password)
        val responseDto = remoteDataSource.login(requestDto)
        val token = TokenMapper.toDomain(responseDto)

        if (!token.isSuccess || token.accessToken.isBlank()) {
            throw Exception("No se pudo iniciar sesión")
        }

        sessionManager.guardarSesion(token.accessToken, token.email)
        return token
    }

    override suspend fun register(
        names: String,
        surnames: String,
        email: String,
        password: String,
        termsAccepted: Boolean
    ) {
        val requestDto = TokenMapper.toRegisterRequest(names, surnames, email, password, termsAccepted)
        remoteDataSource.register(requestDto)
    }

    override fun logout() {
        sessionManager.cerrarSesion()
    }
}
