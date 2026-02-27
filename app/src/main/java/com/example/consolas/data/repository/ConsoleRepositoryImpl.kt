package com.example.consolas.data.repository

import com.example.consolas.data.local.ConsoleDao
import com.example.consolas.data.local.SessionManager
import com.example.consolas.data.local.toEntity
import com.example.consolas.data.local.toDomain
import com.example.consolas.data.model.toDomain
import com.example.consolas.data.model.toRequest
import com.example.consolas.data.service.ApiService
import com.example.consolas.domain.model.Console
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
                    // AQUÍ ES DONDE OCURRE EL MILAGRO:
                    // Guardamos las 10 de la API en Room
                    dao.upsertAll(remoteList.map { it.toEntity(email) })
                    android.util.Log.d("DEBUG_API", "Sincronización exitosa: ${remoteList.size} consolas guardadas.")
                }
                remoteList
            } else {
                dao.getListSync(email).map { it.toDomain() }
            }
        } catch (e: Exception) {
            android.util.Log.e("DEBUG_API", "Sigue fallando la red: ${e.message}")
            // Mientras falle, Room seguirá vacío si es la primera vez
            dao.getListSync(email).map { it.toDomain() }
        }
    }

    override suspend fun addConsole(console: Console) {
        val email = sessionManager.userEmail()
        val entity = console.toEntity(email)
        dao.upsertAll(listOf(entity))

        android.util.Log.d("DEBUG_ROOM", "Consola guardada: ${entity.name} para el email: $email")
    }

    override suspend fun deleteConsole(consoleName: String) {
        val email = sessionManager.userEmail()
        try {
            api.deleteConsole(consoleName)
        } catch (_: Exception) {}
        dao.deleteByName(email, consoleName)
    }

    override suspend fun editConsole(position: Int, console: Console) {
        val email = sessionManager.userEmail()
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
        dao.upsertAll(listOf(console.toEntity(email)))
    }

    override suspend fun updateConsole(name: String, update: UpdateConsole): Console? {
        val email = sessionManager.userEmail()
        return try {
            val response = api.updateConsole(name, update)
            if (response.isSuccessful) {
                val updated = response.body()?.toDomain()
                updated?.let { dao.upsertAll(listOf(it.toEntity(email))) }
                updated
            } else null
        } catch (_: Exception) { null }
    }
}