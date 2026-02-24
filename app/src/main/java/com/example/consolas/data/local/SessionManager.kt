package com.example.consolas.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_EMAIL = "user_email"
        private const val KEY_NAME = "user_name"
    }

    /**
     * Guarda los datos del usuario tras un login exitoso.
     * Es vital llamar a esta función en LoginActivity para que el resto
     * de la app pueda identificar al usuario en Room.
     */
    fun setUser(email: String, name: String) {
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAME, name)
            .apply()
    }

    /**
     * Recupera el email del usuario actual.
     * Si devuelve "", las consultas de Room fallarán al no encontrar al usuario.
     */
    fun userEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    /**
     * Recupera el nombre del usuario actual.
     */
    fun userName(): String = prefs.getString(KEY_NAME, "") ?: ""

    /**
     * Limpia la sesión actual (Logout).
     */
    fun clear() = prefs.edit().clear().apply()
}