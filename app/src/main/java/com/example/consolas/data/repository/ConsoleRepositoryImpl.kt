package com.example.consolas.data.repository

import com.example.consolas.domain.model.Console
import com.example.consolas.domain.repository.ConsoleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsoleRepositoryImpl @Inject constructor() : ConsoleRepository {

    // Aquí es donde vive tu lista en memoria por ahora
    private val listConsoles: MutableList<Console> = mutableListOf()

    suspend override fun getConsoles(): List<Console> = listConsoles

    suspend override fun addConsole(console: Console) {
        listConsoles.add(console)
    }

    suspend override fun deleteConsole(console: Console) {
        listConsoles.remove(console)
    }

    suspend override fun editConsole(position: Int, console: Console) {
        if (position in listConsoles.indices) {
            listConsoles[position] = console
        }
    }
}