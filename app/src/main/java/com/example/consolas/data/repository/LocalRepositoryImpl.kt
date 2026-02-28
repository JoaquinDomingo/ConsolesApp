package com.example.consolas.data.repository

import com.example.consolas.data.local.ConsoleDao
import com.example.consolas.data.local.toDomain
import com.example.consolas.data.local.toEntity
import com.example.consolas.data.model.toRequest
import com.example.consolas.data.service.ApiService
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.Game
import com.example.consolas.domain.repository.LocalRepository
import com.example.consolas.domain.repository.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepositoryImpl @Inject constructor(
    private val consoleDao: ConsoleDao,
    private val apiService: ApiService
) : LocalRepository {

    override fun observeLocalConsoles(userEmail: String): Flow<List<Console>> =
        consoleDao.observeAll(userEmail).map { list ->
            list.map { it.toDomain() }
        }

    override fun observeFavoriteConsoles(userEmail: String): Flow<List<Console>> =
        consoleDao.observeFavorites(userEmail).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun upsertConsoles(userEmail: String, consoles: List<Console>) {
        if (userEmail.isBlank()) return
        val local = consoleDao.getListSync(userEmail)

        val entities = consoles.map { apiConsole ->
            val localMatch = local.find { it.name == apiConsole.name }

            // Solo dejamos que la API pise los datos si trae información nueva (más juegos)
            // o si la consola no existía localmente.
            if (localMatch != null && (localMatch.nativeGames.size + localMatch.adaptedGames.size) >
                (apiConsole.nativeGames.size + apiConsole.adaptedGames.size)) {
                localMatch // Mantenemos la local que tiene el juego 11
            } else {
                // Usamos la de la API pero mantenemos el favorito local
                val isFav = localMatch?.favorite ?: apiConsole.favorite
                apiConsole.toEntity(userEmail).copy(favorite = isFav)
            }
        }

        if (entities.isNotEmpty()) consoleDao.upsertAll(entities)
    }

    override suspend fun setFavorite(userEmail: String, name: String, favorite: Boolean) {
        if (userEmail.isNotBlank()) {
            consoleDao.setFavorite(userEmail, name, favorite)
        }
    }

    override suspend fun deleteLocalConsole(userEmail: String, name: String) {
        if (userEmail.isNotBlank()) {
            consoleDao.deleteByName(userEmail, name)
        }
    }

    override suspend fun getUserStats(userEmail: String): UserStats {
        if (userEmail.isBlank()) return UserStats(0, 0.0)
        val favs = consoleDao.favoritesCount(userEmail)
        val avg = consoleDao.averagePrice(userEmail) ?: 0.0
        return UserStats(favs, avg)
    }

    override suspend fun addGameToConsole(
        consoleName: String,
        game: Game,
        isNative: Boolean,
        userEmail: String
    ): Boolean {
        return try {
            val response = apiService.addGameToConsole(consoleName, isNative, game.toRequest())

            if (response.isSuccessful) {
                val entity = consoleDao.getConsoleByNameSync(userEmail, consoleName)

                entity?.let { currentEntity ->
                    val domain = currentEntity.toDomain()
                    val newNative = domain.nativeGames.toMutableList()
                    val newAdapted = domain.adaptedGames.toMutableList()

                    if (isNative) newNative.add(game) else newAdapted.add(game)

                    val updatedDomain = domain.copy(nativeGames = newNative, adaptedGames = newAdapted)

                    consoleDao.upsertAll(listOf(updatedDomain.toEntity(userEmail)))

                    android.util.Log.d("DEBUG_INSTANT", "Juego inyectado para el usuario ACTUAL: $userEmail")
                }
                true
            } else false
        } catch (e: Exception) { false }
    }
}