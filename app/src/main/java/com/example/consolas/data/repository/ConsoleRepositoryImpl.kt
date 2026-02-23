package com.example.consolas.data.repository

import com.example.consolas.data.model.ConsoleRequest
import com.example.consolas.data.model.toRequest

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


    override suspend fun addConsole(console: Console) {
        try {
            api.addConsole(console.toRequest())
        } catch (_: Exception) {}
    }

    override suspend fun deleteConsole(console: String) {
        try {
            api.deleteConsole(console)
        } catch (_: Exception) {}
    }


    override suspend fun editConsole(position: Int, console: Console) {
        try {
            val update = UpdateConsole(
                name = console.name,
                releasedate = console.releasedate,
                company = console.company,
                description = console.description,
                image = console.image,
                price = console.price,
                favorite = console.favorite,
                nativeGames = console.nativeGames,
                adaptedGames = console.adaptedGames
            )

            api.updateConsole(console.name, update)
        } catch (_: Exception) {}
    }


    override suspend fun updateConsole(
        name: String,
        update: UpdateConsole
    ): Console? {
         return try {
            val response = api.updateConsole(name, update)
            if (response.isSuccessful) response.body()?.toDomain() else null
        } catch (_: Exception) {
            null
        }
        TODO("Revisar esta funcion")
    }
}