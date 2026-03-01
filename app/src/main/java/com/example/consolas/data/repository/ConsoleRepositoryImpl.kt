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
        return try {
            val response = api.getConsoles()
            if (response.isSuccessful) {
                val body = response.body()

                // LOG DE CONTROL: ¿Viene algo de la nube?
                android.util.Log.d("API_CHECK", "Consolas recibidas: ${body?.size ?: 0}")

                val remoteList = body?.map { it.toDomain() } ?: emptyList()

                if (remoteList.isNotEmpty()) {
                    // IMPORTANTE: Guardamos usando el email que ya trae la consola de la API
                    dao.upsertAll(remoteList.map { it.toEntity(it.userEmail) })
                }
                remoteList
            } else {
                android.util.Log.e("API_CHECK", "Error en API: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("API_CHECK", "Excepción: ${e.message}")
            emptyList()
        }
    }

    override suspend fun addConsole(console: Console) {
        try {
            api.addConsole(console.toRequest())
        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Error al añadir: ${e.message}")
        }
        // CORRECCIÓN: Usamos el email que ya trae la consola (que es el tuyo al crearla)
        dao.upsertAll(listOf(console.toEntity(console.userEmail)))
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
                favorite = console.favorite
            )
            api.updateConsole(console.name, update)
        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Error en editConsole: ${e.message}")
        }
        // CORRECCIÓN: Pasamos el email del objeto console
        dao.upsertAll(listOf(console.toEntity(console.userEmail)))
    }

    override suspend fun updateConsole(name: String, update: UpdateConsole): Console? {
        val myEmail = sessionManager.userEmail()
        return try {
            val response = api.updateConsole(name, update)
            if (response.isSuccessful) {
                val updated = response.body()?.toDomain()
                updated?.let {
                    if (update.name != null && update.name != name) {
                        dao.deleteByName(myEmail, name)
                    }
                    // CORRECCIÓN: Pasamos el email que devuelve el servidor en el objeto actualizado
                    dao.upsertAll(listOf(it.toEntity(it.userEmail)))
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
                // Refrescamos la lista local para obtener los juegos nuevos
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
        val myEmail = sessionManager.userEmail()
        try {
            api.deleteConsole(consoleName)
        } catch (_: Exception) {}
        dao.deleteByName(myEmail, consoleName)
    }
}