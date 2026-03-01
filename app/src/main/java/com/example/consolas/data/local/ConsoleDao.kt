package com.example.consolas.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsoleDao {
    @Query("SELECT * FROM consoles ORDER BY name ASC")
    fun observeAll(): Flow<List<ConsoleEntity>>

    @Query("SELECT * FROM consoles WHERE userEmail = :email")
    fun observeConsolesByEmail(email: String): Flow<List<ConsoleEntity>>

    @Query("SELECT * FROM consoles WHERE userEmail = :userEmail AND favorite = 1")
    fun observeFavorites(userEmail: String): Flow<List<ConsoleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ConsoleEntity>)

    @Query("SELECT * FROM consoles WHERE name = :name LIMIT 1")
    suspend fun getConsoleByNameSync(name: String): ConsoleEntity?

    @Query("SELECT * FROM consoles WHERE userEmail = :userEmail")
    suspend fun getListSync(userEmail: String): List<ConsoleEntity>

    @Query("UPDATE consoles SET favorite = :favorite WHERE userEmail = :userEmail AND name = :name")
    suspend fun setFavorite(userEmail: String, name: String, favorite: Boolean)

    @Query("DELETE FROM consoles WHERE userEmail = :userEmail AND name = :name")
    suspend fun deleteByName(userEmail: String, name: String)

    @Query("SELECT COUNT(*) FROM consoles WHERE userEmail = :userEmail AND favorite = 1")
    suspend fun favoritesCount(userEmail: String): Int

    @Query("SELECT AVG(price) FROM consoles WHERE userEmail = :userEmail")
    suspend fun averagePrice(userEmail: String): Double?
}