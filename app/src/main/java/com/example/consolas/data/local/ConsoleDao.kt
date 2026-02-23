package com.example.consolas.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsoleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ConsoleEntity>)

    @Query("SELECT * FROM consoles WHERE userEmail = :userEmail ORDER BY name ASC")
    fun observeAll(userEmail: String): Flow<List<ConsoleEntity>>

    @Query("SELECT * FROM consoles WHERE userEmail = :userEmail AND favorite = 1 ORDER BY name ASC")
    fun observeFavorites(userEmail: String): Flow<List<ConsoleEntity>>

    @Query("UPDATE consoles SET favorite = :favorite WHERE userEmail = :userEmail AND name = :name")
    suspend fun setFavorite(userEmail: String, name: String, favorite: Boolean)

    @Query("DELETE FROM consoles WHERE userEmail = :userEmail AND name = :name")
    suspend fun deleteByName(userEmail: String, name: String)

    @Query("SELECT COUNT(*) FROM consoles WHERE userEmail = :userEmail AND favorite = 1")
    suspend fun favoritesCount(userEmail: String): Int

    @Query("SELECT AVG(price) FROM consoles WHERE userEmail = :userEmail")
    suspend fun averagePrice(userEmail: String): Double?
}
