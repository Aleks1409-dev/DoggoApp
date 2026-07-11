package com.grupo06.doggoapp

import android.app.Application
import com.grupo06.doggoapp.di.AppContainer

class DoggoApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
