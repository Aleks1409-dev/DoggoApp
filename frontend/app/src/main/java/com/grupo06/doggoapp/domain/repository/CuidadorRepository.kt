package com.grupo06.doggoapp.domain.repository

import com.grupo06.doggoapp.domain.model.Cuidador

interface CuidadorRepository {
    suspend fun getCuidadores(): List<Cuidador>
}
