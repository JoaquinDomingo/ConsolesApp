package com.example.consolas.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE userEmail = :userEmail ORDER BY timestamp ASC")
    fun observeMessages(userEmail: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    @Query("DELETE FROM messages WHERE userEmail = :userEmail")
    suspend fun deleteAll(userEmail: String)
}
