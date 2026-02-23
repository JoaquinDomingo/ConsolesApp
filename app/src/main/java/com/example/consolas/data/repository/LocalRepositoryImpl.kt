package com.example.consolas.data.repository

import com.example.consolas.data.local.ConsoleDao
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
        consoleDao.observeAll(userEmail).map { list -> list.map { it.toDomain() } }

    override fun observeFavoriteConsoles(userEmail: String): Flow<List<Console>> =
        consoleDao.observeFavorites(userEmail).map { list -> list.map { it.toDomain() } }

    override suspend fun upsertConsoles(userEmail: String, consoles: List<Console>) {
        consoleDao.upsertAll(consoles.map { it.toEntity(userEmail) })
    }

    override suspend fun setFavorite(userEmail: String, name: String, favorite: Boolean) {
        consoleDao.setFavorite(userEmail, name, favorite)
    }

    override suspend fun deleteLocalConsole(userEmail: String, name: String) {
        consoleDao.deleteByName(userEmail, name)
    }

    override suspend fun getUserStats(userEmail: String): UserStats {
        val favs = consoleDao.favoritesCount(userEmail)
        val avg = consoleDao.averagePrice(userEmail) ?: 0.0
        return UserStats(favoritesCount = favs, averagePrice = avg)
    }
}
