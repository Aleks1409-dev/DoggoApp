package com.grupo06.doggoapp.data.repository

import com.grupo06.doggoapp.data.remote.datasource.RemoteDataSource
import com.grupo06.doggoapp.domain.model.Cuidador
import com.grupo06.doggoapp.domain.repository.CuidadorRepository

class CuidadorRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : CuidadorRepository {

    override suspend fun getCuidadores(): List<Cuidador> {
        val response = remoteDataSource.getCuidadores()
        val listaCuidadores = mutableListOf<Cuidador>()

        if (response != null && response.containsKey("sitters")) {
            val sittersList = response["sitters"] as? List<Map<String, Any>>

            sittersList?.forEach { map ->
                listaCuidadores.add(
                    Cuidador(
                        nombre = map["name"]?.toString() ?: "Sin Nombre",
                        ubicacion = "Lima",
                        rating = 5.0,
                        precio = 30,
                        tipo = "Paseo"
                    )
                )
            }
        }
        return listaCuidadores
    }
}