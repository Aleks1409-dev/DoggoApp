package com.grupo06.doggoapp.core.session

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun guardarSesion(token: String, email: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun obtenerToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun obtenerEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun haySesionActiva(): Boolean = !obtenerToken().isNullOrBlank()

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS_NAME = "doggo_session"
        const val KEY_TOKEN = "token"
        const val KEY_EMAIL = "email"
    }
}
