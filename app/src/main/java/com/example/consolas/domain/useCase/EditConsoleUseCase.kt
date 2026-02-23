package com.example.consolas.domain.useCase

import com.example.consolas.data.repository.Repository
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.UpdateConsole
import com.example.consolas.domain.repository.ConsoleRepository
import javax.inject.Inject

class EditConsoleUseCase @Inject constructor(private val repository: ConsoleRepository) {
    suspend operator fun invoke(name: String, update: UpdateConsole) =
        repository.updateConsole(name, update)
}