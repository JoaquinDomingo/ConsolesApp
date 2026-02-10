package com.example.consolas.domain.useCase

import com.example.consolas.data.repository.Repository
import javax.inject.Inject

class EditConsoleUseCase @Inject constructor
    (private val repository: Repository)
{
    suspend operator fun invoke(position: Int, updatedConsole: com.example.consolas.domain.model.Console) =
        repository.editConsole(position, updatedConsole)
}