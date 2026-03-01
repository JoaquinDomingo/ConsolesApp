package com.example.consolas.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    // Cambiado: Ahora filtra por el emisor O el receptor, asegurando que solo veas
    // los mensajes donde TÚ participas (Cristian con X).
    @Query("""
        SELECT * FROM messages 
        WHERE (senderEmail = :userEmail OR receiverEmail = :userEmail)
        ORDER BY timestamp ASC
    """)
    fun observeMessages(userEmail: String): Flow<List<MessageEntity>>

    // Cambiado: Solo muestra el último mensaje de cada chat donde TÚ eres uno de los participantes.
    // Evita que Joaquín vea en su lista chats ajenos.
    @Query("""
        SELECT * FROM messages 
        WHERE id IN (
            SELECT MAX(id) 
            FROM messages 
            WHERE senderEmail = :myEmail OR receiverEmail = :myEmail 
            GROUP BY CASE 
                WHEN senderEmail = :myEmail THEN receiverEmail 
                ELSE senderEmail 
            END
        ) 
        ORDER BY timestamp DESC
    """)
    fun observeLastMessages(myEmail: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: MessageEntity)

    @Query("DELETE FROM messages WHERE senderEmail = :userEmail OR receiverEmail = :userEmail")
    suspend fun deleteAll(userEmail: String)
}