package com.example.consolas.data.repository

import com.example.consolas.data.local.MessageDao
import com.example.consolas.data.local.MessageEntity
import com.example.consolas.domain.repository.Message
import com.example.consolas.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao
) : MessageRepository {

    override fun observeMessages(userEmail: String): Flow<List<Message>> =
        messageDao.observeMessages(userEmail).map { list ->
            list.map { Message(it.id, it.text, it.timestamp, it.fromUser) }
        }

    override suspend fun sendMessage(userEmail: String, text: String, fromUser: Boolean) {
        val entity = MessageEntity(
            userEmail = userEmail,
            text = text.trim(),
            timestamp = System.currentTimeMillis(),
            fromUser = fromUser
        )
        messageDao.insert(entity)
    }

    override suspend fun deleteAll(userEmail: String) {
        messageDao.deleteAll(userEmail)
    }
}
