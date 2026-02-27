package com.example.consolas.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsoleDao {
    @Query("SELECT * FROM consoles WHERE userEmail = :email")
    fun observeConsoles(email: String): Flow<List<ConsoleEntity>> // Cambiado a Flow
    // Inserta o reemplaza una lista de consolas (Útil para la carga inicial de API)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ConsoleEntity>)

    // Actualiza una consola existente (Vital para cambiar el estado 'favorite')
    @Update
    suspend fun updateConsole(console: ConsoleEntity)

    // Observa todas las consolas de un usuario específico
    @Query("SELECT * FROM consoles WHERE userEmail = :userEmail ORDER BY name ASC")
    fun observeAll(userEmail: String): Flow<List<ConsoleEntity>>

    // Observa solo las consolas favoritas de un usuario específico
    @Query("SELECT * FROM consoles WHERE userEmail = :userEmail AND favorite = 1 ORDER BY name ASC")
    fun observeFavorites(userEmail: String): Flow<List<ConsoleEntity>>

    // Actualización rápida del estado de favorito por campos
    @Query("UPDATE consoles SET favorite = :favorite WHERE userEmail = :userEmail AND name = :name")
    suspend fun setFavorite(userEmail: String, name: String, favorite: Boolean)

    // Borra una consola específica de un usuario
    @Query("DELETE FROM consoles WHERE userEmail = :userEmail AND name = :name")
    suspend fun deleteByName(userEmail: String, name: String)

    // Cuenta cuántos favoritos tiene un usuario
    @Query("SELECT COUNT(*) FROM consoles WHERE userEmail = :userEmail AND favorite = 1")
    suspend fun favoritesCount(userEmail: String): Int

    // Calcula el precio medio de la colección de un usuario
    @Query("SELECT AVG(price) FROM consoles WHERE userEmail = :userEmail")
    suspend fun averagePrice(userEmail: String): Double?

    // Obtiene la lista de forma síncrona (Usada en el Repositorio para buscar entidades antes de actualizar)
    @Query("SELECT * FROM consoles WHERE userEmail = :userEmail")
    suspend fun getListSync(userEmail: String): List<ConsoleEntity>
}