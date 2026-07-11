package com.grupo06.doggoapp.data.remote.dto

data class LoginResponseDto(
    val success: String,
    val token: String,
    val email: String
)
