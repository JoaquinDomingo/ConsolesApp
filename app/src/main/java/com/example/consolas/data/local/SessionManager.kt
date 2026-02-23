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

    fun setUser(email: String, name: String) {
        prefs.edit()
            .putString("user_email", email)
            .putString("user_name", name)
            .apply()
    }

    fun userEmail(): String = prefs.getString("user_email", "") ?: ""
    fun userName(): String = prefs.getString("user_name", "") ?: ""
    fun clear() = prefs.edit().clear().apply()
}
