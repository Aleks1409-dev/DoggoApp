package com.grupo06.doggoapp.domain.model

data class Token(
    val accessToken: String,
    val email: String,
    val isSuccess: Boolean
)
