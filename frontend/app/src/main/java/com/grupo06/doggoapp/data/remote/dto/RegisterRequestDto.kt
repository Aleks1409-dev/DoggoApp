package com.grupo06.doggoapp.data.remote.dto

data class RegisterRequestDto(
    val names: String,
    val surnames: String,
    val email: String,
    val password: String,
    val terms_accepted: Boolean
)
