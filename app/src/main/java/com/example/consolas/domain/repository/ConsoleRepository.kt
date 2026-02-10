package com.example.consolas.domain.repository

import com.example.consolas.domain.model.Console

interface ConsoleRepository {
    suspend fun getConsoles(): List<Console>
    suspend fun addConsole(console: Console)
    suspend fun deleteConsole(console: Console)
    suspend fun editConsole(position: Int, console: Console)
}