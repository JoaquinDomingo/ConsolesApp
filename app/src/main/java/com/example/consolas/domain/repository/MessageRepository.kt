package com.example.consolas.domain.repository

import com.example.consolas.data.local.MessageEntity
import kotlinx.coroutines.flow.Flow

data class Message(
    val id: Long,
    val text: String,
    val timestamp: Long,
    val sender: String,
    val receiver: String
)

interface MessageRepository {
    fun observeMessages(userEmail: String): Flow<List<Message>>
    suspend fun sendMessage(userEmail: String, text: String, fromUser: Boolean = true)
    suspend fun deleteAll(userEmail: String)
    fun startRealtimeChat(myEmail: String)
    suspend fun syncWithServer(otherUserEmail: String)

    fun observeLastMessages(): Flow<List<MessageEntity>>
}
