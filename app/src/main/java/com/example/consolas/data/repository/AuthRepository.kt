package com.example.consolas.data.repository

import com.example.consolas.data.local.SessionManager
import com.example.consolas.data.model.UserRequest
import com.example.consolas.data.service.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    suspend fun login(email: String, pass: String, name: String): Boolean {
        return try {
            val response = apiService.login(UserRequest(email, pass))
            if (response.isSuccessful && response.body() != null) {
                // Guardamos Token, Email y el Nombre que viene del campo etUser
                sessionManager.saveAuthData(response.body()!!.token, email, name)
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun register(email: String, pass: String): Boolean {
        val response = apiService.register(UserRequest(email, pass))
        return response.isSuccessful
    }
}