package com.example.consolas.domain.useCase

import com.example.consolas.data.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, pass: String): Result<Boolean> {
        return if (email.isBlank() || pass.isBlank()) {
            Result.failure(Exception("Datos de registro inválidos"))
        } else {
            val success = repository.register(email, pass)
            if (success) Result.success(true)
            else Result.failure(Exception("El usuario ya existe o error en servidor"))
        }
    }
}