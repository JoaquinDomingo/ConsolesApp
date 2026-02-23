package com.example.consolas.data.repository

import com.example.consolas.data.service.ApiService
import com.example.consolas.data.model.toDomain
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.UpdateConsole
import com.example.consolas.domain.repository.ConsoleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsoleRepositoryImpl @Inject constructor(
    private val api: ApiService
) : ConsoleRepository {

    override suspend fun getConsoles(): List<Console> {
        return try {
            val response = api.getConsoles()
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addConsole(console: Console) {}

    override suspend fun deleteConsole(console: String) {}

    override suspend fun editConsole(position: Int, console: Console) {}
    override suspend fun updateConsole(
        name: String,
        update: UpdateConsole
    ): Console? {
        TODO("Not yet implemented")
    }
}