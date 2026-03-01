package com.example.consolas.domain.useCase

import com.example.consolas.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, pass: String, name: String): Result<Boolean> {
        return if (email.isBlank() || pass.isBlank() || name.isBlank()) {
            Result.failure(Exception("Todos los campos son obligatorios"))
        } else {
            val success = repository.login(email, pass, name)
            if (success) Result.success(true)
            else Result.failure(Exception("Error al iniciar sesión"))
        }
    }
}