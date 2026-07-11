package com.grupo06.doggoapp.data.repository

import com.grupo06.doggoapp.data.remote.datasource.RemoteDataSource
import com.grupo06.doggoapp.domain.model.Cuidador
import com.grupo06.doggoapp.domain.repository.CuidadorRepository

class CuidadorRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : CuidadorRepository {

    // The CuidadoresApi endpoint's real JSON shape isn't confirmed against the deployed
    // backend yet (see CLAUDE.md "Known gap"), so parsing is deliberately deferred here.
    override suspend fun getCuidadores(): List<Cuidador> {
        remoteDataSource.getCuidadores()
        return emptyList()
    }
}
