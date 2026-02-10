package com.example.consolas.domain.useCase

import com.example.consolas.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, pass: String) = repository.login(email, pass)
}