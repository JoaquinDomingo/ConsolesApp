package com.example.consolas.domain.useCase

import com.example.consolas.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, pass: String): Result<Boolean> {
        return if (email.isBlank() || pass.isBlank()) {
            Result.failure(Exception("Los campos no pueden estar vacíos"))
        } else {
            val success = repository.login(email, pass)
            if (success) Result.success(true)
            else Result.failure(Exception("Email o contraseña incorrectos"))
        }
    }
}