package com.grupo06.doggoapp.di

import android.content.Context
import com.grupo06.doggoapp.core.network.ApiService
import com.grupo06.doggoapp.core.network.RetrofitClient
import com.grupo06.doggoapp.core.session.SessionManager

class NetworkModule(context: Context) {

    val sessionManager: SessionManager by lazy { SessionManager(context) }

    private val retrofitClient: RetrofitClient by lazy { RetrofitClient(sessionManager) }

    val apiService: ApiService by lazy { retrofitClient.api }
}
