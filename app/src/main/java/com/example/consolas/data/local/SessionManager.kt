package com.example.consolas.data.local

import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "user_token"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_NAME = "user_name"
        private const val KEY_IMAGE_PREFIX = "profile_image_"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
    }

    /**
     * Guarda toda la sesión de golpe.
     * Al incluir el 'name', nos aseguramos de que el valor de etUser se guarde
     * junto al token y el email en un solo paso.
     */
    fun saveAuthData(token: String, email: String, name: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAME, name)
            .apply()
    }

    /**
     * Permite actualizar el nombre o email de forma independiente
     * (Útil para el EditProfileFragment).
     */
    fun setUser(email: String, name: String) {
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAME, name)
            .apply()
    }

    /**
     * Guarda la imagen solicitando permisos persistentes para que la galería
     * no se borre al reiniciar la App.
     */
    fun saveProfileImage(uriString: String) {
        val email = userEmail()
        if (email.isNotEmpty()) {
            try {
                val uri = Uri.parse(uriString)
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            prefs.edit().putString(KEY_IMAGE_PREFIX + email, uriString).apply()
        }
    }

    // --- Getters ---

    fun getProfileImage(): String? {
        val email = userEmail()
        return if (email.isNotEmpty()) prefs.getString(KEY_IMAGE_PREFIX + email, null) else null
    }

    fun getUserToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun isUserLoggedIn(): Boolean = !getUserToken().isNullOrEmpty()

    fun userEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    fun userName(): String = prefs.getString(KEY_NAME, "") ?: ""

    // --- Configuración ---

    fun logout() {
        // Borramos la sesión activa pero mantenemos las rutas de las fotos
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_EMAIL)
            .remove(KEY_NAME)
            .apply()
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean = prefs.getBoolean(KEY_NOTIFICATIONS, true)
}