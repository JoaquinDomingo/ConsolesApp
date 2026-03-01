package com.example.consolas.data.repository

import com.example.consolas.data.local.*
import com.example.consolas.data.model.toRequest
import com.example.consolas.data.service.ApiService
import com.example.consolas.domain.model.*
import com.example.consolas.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepositoryImpl @Inject constructor(
    private val consoleDao: ConsoleDao,
    private val apiService: ApiService
) : LocalRepository {

    override fun observeAllConsoles(): Flow<List<Console>> =
        consoleDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeLocalConsoles(userEmail: String): Flow<List<Console>> =
        consoleDao.observeConsolesByEmail(userEmail).map { list -> list.map { it.toDomain() } }

    override fun observeFavoriteConsoles(userEmail: String): Flow<List<Console>> =
        consoleDao.observeFavorites(userEmail).map { list -> list.map { it.toDomain() } }

    override suspend fun upsertConsoles(consoles: List<Console>) {
        val entities = consoles.map { apiConsole ->
            // Buscamos si ya existe para no perder el favorito
            val localMatch = consoleDao.getConsoleByNameSync(apiConsole.name)
            val isFav = localMatch?.favorite ?: apiConsole.favorite

            // Usamos el email que viene de la API
            apiConsole.toEntity(apiConsole.userEmail).copy(favorite = isFav)
        }
        // Al usar REPLACE en el DAO, si el nombre ya existe, se actualiza la fila
        if (entities.isNotEmpty()) consoleDao.upsertAll(entities)
    }

    override suspend fun setFavorite(userEmail: String, name: String, favorite: Boolean) =
        consoleDao.setFavorite(userEmail, name, favorite)

    override suspend fun deleteLocalConsole(userEmail: String, name: String) =
        consoleDao.deleteByName(userEmail, name)

    override suspend fun getUserStats(userEmail: String): UserStats =
        UserStats(consoleDao.favoritesCount(userEmail), consoleDao.averagePrice(userEmail) ?: 0.0)

    override suspend fun addGameToConsole(consoleName: String, game: Game, isNative: Boolean, userEmail: String): Boolean {
        return try {
            val response = apiService.addGameToConsole(consoleName, isNative, game.toRequest())
            if (response.isSuccessful) {
                consoleDao.getConsoleByNameSync(consoleName)?.let { entity ->
                    val domain = entity.toDomain()
                    val newNative = domain.nativeGames.toMutableList()
                    val newAdapted = domain.adaptedGames.toMutableList()
                    if (isNative) newNative.add(game) else newAdapted.add(game)
                    val updated = domain.copy(nativeGames = newNative, adaptedGames = newAdapted)
                    consoleDao.upsertAll(listOf(updated.toEntity(updated.userEmail)))
                }
                true
            } else false
        } catch (e: Exception) { false }
    }
}