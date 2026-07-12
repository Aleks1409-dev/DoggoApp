package com.grupo06.doggoapp.core.network

import com.grupo06.doggoapp.core.session.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient(sessionManager: SessionManager) {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(sessionManager))
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://rg65zent76.execute-api.us-east-1.amazonaws.com/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}