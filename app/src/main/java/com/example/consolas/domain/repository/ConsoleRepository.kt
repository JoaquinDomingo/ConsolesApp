package com.example.consolas.domain.repository

import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.UpdateConsole

interface ConsoleRepository {
    suspend fun getConsoles(): List<Console>
    suspend fun addConsole(console: Console)
    suspend fun deleteConsole(console: String)
    suspend fun editConsole(position: Int, console: Console)
    suspend fun updateConsole(name: String, update: UpdateConsole): Console?
}