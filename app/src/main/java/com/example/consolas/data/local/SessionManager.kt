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
        private const val KEY_PROFILE_IMAGE = "profile_image"
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
     * Guarda la URI de la imagen de perfil seleccionada por el usuario.
     * Esto permite que la foto persista aunque se cierre la aplicación.
     */
    fun saveProfileImage(uri: String) {
        prefs.edit()
            .putString(KEY_PROFILE_IMAGE, uri)
            .apply()
    }

    /**
     * Recupera la URI de la imagen de perfil guardada.
     * Devuelve null si el usuario aún no ha establecido ninguna foto.
     */
    fun getProfileImage(): String? = prefs.getString(KEY_PROFILE_IMAGE, null)

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
     * Borra todos los datos almacenados, incluyendo email, nombre y foto de perfil.
     */
    fun clear() = prefs.edit().clear().apply()
}