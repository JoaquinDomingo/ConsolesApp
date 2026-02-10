package com.example.consolas.domain.repository

interface AuthRepository {
    suspend fun login(email: String, pass: String): Result<Boolean>
    suspend fun register(email: String, pass: String): Result<Boolean>
    fun getCurrentUserEmail(): String?
    fun logout()
}