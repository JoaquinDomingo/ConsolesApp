package com.example.consolas.data.repository

import com.example.consolas.data.local.ConsoleDao
import com.example.consolas.data.local.SessionManager
import com.example.consolas.data.local.toEntity
import com.example.consolas.data.local.toDomain
import com.example.consolas.data.model.toDomain
import com.example.consolas.data.model.toRequest
import com.example.consolas.data.service.ApiService
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.Game
import com.example.consolas.domain.model.UpdateConsole
import com.example.consolas.domain.repository.ConsoleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsoleRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val dao: ConsoleDao,
    private val sessionManager: SessionManager
) : ConsoleRepository {

    override suspend fun getConsoles(): List<Console> {
        val email = sessionManager.userEmail()
        return try {
            val response = api.getConsoles()
            if (response.isSuccessful) {
                val remoteList = response.body()?.map { it.toDomain() } ?: emptyList()
                if (remoteList.isNotEmpty()) {
                    dao.upsertAll(remoteList.map { it.toEntity(email) })
                }
                remoteList
            } else {
                dao.getListSync(email).map { it.toDomain() }
            }
        } catch (e: Exception) {
            dao.getListSync(email).map { it.toDomain() }
        }
    }

    override suspend fun addConsole(console: Console) {
        val email = sessionManager.userEmail()
        try {
            api.addConsole(console.toRequest())
        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Error al añadir: ${e.message}")
        }
        dao.upsertAll(listOf(console.toEntity(email)))
    }

    override suspend fun editConsole(position: Int, console: Console) {
        val email = sessionManager.userEmail()
        try {
            // Convertimos la consola actual a un UpdateConsole para el PATCH
            val update = UpdateConsole(
                name = console.name,
                releasedate = console.releasedate,
                company = console.company,
                description = console.description,
                image = console.image,
                price = console.price,
                favorite = console.favorite
            )
            api.updateConsole(console.name, update)
        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Error en editConsole: ${e.message}")
        }
        dao.upsertAll(listOf(console.toEntity(email)))
    }

    override suspend fun updateConsole(name: String, update: UpdateConsole): Console? {
        val email = sessionManager.userEmail()
        return try {
            val response = api.updateConsole(name, update)
            if (response.isSuccessful) {
                val updated = response.body()?.toDomain()
                updated?.let {
                    if (update.name != null && update.name != name) {
                        dao.deleteByName(email, name)
                    }
                    dao.upsertAll(listOf(it.toEntity(email)))
                }
                updated
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addGameToConsole(consoleName: String, game: Game, isNative: Boolean): Boolean {
        return try {
            val response = api.addGameToConsole(consoleName, isNative, game.toRequest())
            if (response.isSuccessful) {
                getConsoles()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteConsole(consoleName: String) {
        val email = sessionManager.userEmail()
        try {
            api.deleteConsole(consoleName)
        } catch (_: Exception) {}
        dao.deleteByName(email, consoleName)
    }
}