package com.example.consolas.domain.repository

import kotlinx.coroutines.flow.Flow

data class Message(
    val id: Long,
    val text: String,
    val timestamp: Long,
    val fromUser: Boolean
)

interface MessageRepository {
    fun observeMessages(userEmail: String): Flow<List<Message>>
    suspend fun sendMessage(userEmail: String, text: String, fromUser: Boolean = true)
    suspend fun deleteAll(userEmail: String)
}
