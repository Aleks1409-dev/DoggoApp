package com.piero.doggoapp.di

import com.piero.doggoapp.data.remote.ApiService
import com.piero.doggoapp.data.repository.CuidadorRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://2ulqi5yey6.execute-api.us-east-1.amazonaws.com/default/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val cuidadorRepository: CuidadorRepository by lazy {
        CuidadorRepository(apiService)
    }
}
