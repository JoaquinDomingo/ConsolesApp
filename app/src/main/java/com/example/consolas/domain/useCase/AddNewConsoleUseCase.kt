package com.example.consolas.domain.useCase

import com.example.consolas.domain.model.Console
import com.example.consolas.domain.repository.ConsoleRepository
import com.example.consolas.domain.repository.LocalRepository
import javax.inject.Inject

class AddNewConsoleUseCase @Inject constructor(
    private val remoteRepository: ConsoleRepository, // Interfaz de la API (MariaDB)
    private val localRepository: LocalRepository      // Interfaz de Room (Local)
) {
    suspend operator fun invoke(console: Console) {
        // 1. Intentamos guardar en la nube (MariaDB)
        // Si la API devuelve un error (ej. 401 o 500), lanzará una excepción aquí
        remoteRepository.addConsole(console)

        // 2. Si la nube funcionó, guardamos en la base de datos local (Room)
        // Pasamos una lista porque tu localRepository.upsertConsoles espera List<Console>
        localRepository.upsertConsoles(listOf(console))
    }
}