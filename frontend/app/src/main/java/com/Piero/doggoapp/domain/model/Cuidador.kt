package com.piero.doggoapp.domain.model

data class Cuidador(
    val nombre: String,
    val ubicacion: String,
    val rating: Double,
    val precio: Int,
    val tipo: String
)

data class ApiData(
    val items: List<Cuidador>
)