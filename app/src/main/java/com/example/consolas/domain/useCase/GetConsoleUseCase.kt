package com.example.consolas.domain.useCase

import com.example.consolas.data.repository.Repository
import com.example.consolas.domain.model.Console
import javax.inject.Inject

class GetConsoleUseCase @Inject constructor(
    private  val repository: Repository)
{
    suspend operator fun invoke(): List<Console> = repository.getConsoles()
}

