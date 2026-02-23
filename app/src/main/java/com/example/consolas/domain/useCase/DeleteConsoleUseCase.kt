package com.example.consolas.domain.useCase

import com.example.consolas.domain.repository.ConsoleRepository
import javax.inject.Inject

class DeleteConsoleUseCase @Inject constructor(private val repository: ConsoleRepository) {
    suspend operator fun invoke(name: String) = repository.deleteConsole(name)
}