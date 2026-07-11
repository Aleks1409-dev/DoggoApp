package com.grupo06.doggoapp.core.network

import com.grupo06.doggoapp.core.session.SessionManager
import com.grupo06.doggoapp.core.utils.Constantes
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient(sessionManager: SessionManager) {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(sessionManager))
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constantes.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
