package com.grupo06.doggoapp.data.mapper

import com.grupo06.doggoapp.data.remote.dto.LoginRequestDto
import com.grupo06.doggoapp.data.remote.dto.LoginResponseDto
import com.grupo06.doggoapp.data.remote.dto.RegisterRequestDto
import com.grupo06.doggoapp.domain.model.Token

object TokenMapper {

    fun toDomain(dto: LoginResponseDto): Token {
        return Token(
            accessToken = dto.token,
            email = dto.email,
            isSuccess = dto.success.equals("true", ignoreCase = true)
        )
    }

    fun toRequest(email: String, password: String): LoginRequestDto {
        return LoginRequestDto(email = email, password = password)
    }

    fun toRegisterRequest(
        names: String,
        surnames: String,
        email: String,
        password: String,
        termsAccepted: Boolean
    ): RegisterRequestDto {
        return RegisterRequestDto(
            names = names,
            surnames = surnames,
            email = email,
            password = password,
            terms_accepted = termsAccepted
        )
    }
}
