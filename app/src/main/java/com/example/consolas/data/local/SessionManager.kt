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
        private const val KEY_TOKEN = "user_token" // Nueva clave para JWT
        private const val KEY_EMAIL = "user_email"
        private const val KEY_NAME = "user_name"
        private const val KEY_IMAGE_PREFIX = "profile_image_"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
    }


    fun saveAuthData(token: String, email: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    // --- Gestión del Token ---

    fun getUserToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun isUserLoggedIn(): Boolean = getUserToken() != null

    // --- Gestión de Perfil ---

    fun setUser(email: String, name: String) {
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAME, name)
            .apply()
    }

    fun saveProfileImage(uri: String) {
        val email = userEmail()
        if (email.isNotEmpty()) {
            prefs.edit().putString(KEY_IMAGE_PREFIX + email, uri).apply()
        }
    }

    fun getProfileImage(): String? {
        val email = userEmail()
        return if (email.isNotEmpty()) prefs.getString(KEY_IMAGE_PREFIX + email, null) else null
    }

    fun userEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""
    fun userName(): String = prefs.getString(KEY_NAME, "") ?: ""

    // --- Configuración y Logout ---

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true)
    }
}