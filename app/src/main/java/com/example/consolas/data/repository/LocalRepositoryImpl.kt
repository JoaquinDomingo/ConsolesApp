package com.example.consolas.data.repository

import com.example.consolas.data.local.ConsoleDao
import com.example.consolas.data.local.ConsoleEntity
import com.example.consolas.data.local.toDomain
import com.example.consolas.data.local.toEntity
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.repository.LocalRepository
import com.example.consolas.domain.repository.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepositoryImpl @Inject constructor(
    private val consoleDao: ConsoleDao
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
        if (userEmail.isBlank()) {
            android.util.Log.e("DEBUG_ROOM", "Upsert cancelado: Email vacío")
            return
        }

        val local = consoleDao.getListSync(userEmail)

        val entities = consoles.map { domain ->
            // Si ya existe en Room, mantenemos su estado de favorito, si no, usamos el de la API
            val isFav = local.find { it.name == domain.name }?.favorite ?: domain.favorite

            domain.toEntity(userEmail).copy(favorite = isFav)
        }

        if (entities.isNotEmpty()) {
            consoleDao.upsertAll(entities)
            android.util.Log.d("DEBUG_ROOM", "Insertadas/Actualizadas ${entities.size} entidades en Room")
        } else {
            android.util.Log.w("DEBUG_ROOM", "No hay entidades para guardar")
        }
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
        if (userEmail.isBlank()) return UserStats(favoritesCount = 0, averagePrice = 0.0)

        val favs = consoleDao.favoritesCount(userEmail)
        val avg = consoleDao.averagePrice(userEmail) ?: 0.0

        return UserStats(favoritesCount = favs, averagePrice = avg)
    }
}