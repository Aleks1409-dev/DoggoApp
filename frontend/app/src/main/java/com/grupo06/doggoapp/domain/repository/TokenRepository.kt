package com.grupo06.doggoapp.domain.repository

import com.grupo06.doggoapp.domain.model.Token

interface TokenRepository {
    suspend fun login(email: String, password: String): Token
    suspend fun register(names: String, surnames: String, email: String, password: String, termsAccepted: Boolean)
    fun logout()
}
