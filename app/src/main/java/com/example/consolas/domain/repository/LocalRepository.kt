package com.example.consolas.domain.repository

import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.Game
import kotlinx.coroutines.flow.Flow

data class UserStats(
    val favoritesCount: Int,
    val averagePrice: Double
)

interface LocalRepository {
    fun observeLocalConsoles(userEmail: String): Flow<List<Console>>
    fun observeFavoriteConsoles(userEmail: String): Flow<List<Console>>
    suspend fun upsertConsoles(userEmail: String, consoles: List<Console>)
    suspend fun setFavorite(userEmail: String, name: String, favorite: Boolean)
    suspend fun deleteLocalConsole(userEmail: String, name: String)
    suspend fun getUserStats(userEmail: String): UserStats

    suspend fun addGameToConsole(consoleName: String, game: Game, isNative: Boolean, userEmail: String): Boolean}
