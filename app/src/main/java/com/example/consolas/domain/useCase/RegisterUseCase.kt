package com.example.consolas.domain.useCase

import com.example.consolas.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, pass: String) = repository.register(email, pass)
}