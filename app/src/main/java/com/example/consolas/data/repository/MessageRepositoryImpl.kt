package com.example.consolas.data.repository

import com.example.consolas.data.local.MessageDao
import com.example.consolas.data.local.MessageEntity
import com.example.consolas.data.local.SessionManager
import com.example.consolas.data.model.ChatMessage
import com.example.consolas.data.service.ApiService
import com.example.consolas.data.service.ChatService
import com.example.consolas.domain.repository.Message
import com.example.consolas.domain.repository.MessageRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val api: ApiService,
    private val chatService: ChatService,
    private val gson: Gson,
    private val sessionManager: SessionManager
) : MessageRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun observeMessages(userEmail: String): Flow<List<Message>> =
        messageDao.observeMessages(userEmail).map { list ->
            list.map {
                Message(
                    id = it.id,
                    text = it.text,
                    timestamp = it.timestamp,
                    sender = it.senderEmail,
                    receiver = it.receiverEmail
                )
            }
        }

    override fun startRealtimeChat(myEmail: String) {
        chatService.connect { json ->
            try {
                val chatMsg = gson.fromJson(json, ChatMessage::class.java)
                repositoryScope.launch {
                    // NORMALIZACIÓN: Redondeamos el tiempo para que coincida con la API
                    val timestamp = (System.currentTimeMillis() / 1000) * 1000

                    saveInternal(
                        sender = chatMsg.sender,
                        receiver = chatMsg.receiver,
                        text = chatMsg.message,
                        sessionEmail = myEmail,
                        timestamp = timestamp
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun syncWithServer(otherUserEmail: String) {
        val myEmail = sessionManager.userEmail()
        try {
            val response = api.getChatHistory(otherUserEmail)
            if (response.isSuccessful) {
                response.body()?.forEach { remoteMsg ->
                    // NORMALIZACIÓN: Usamos el mismo criterio que en el socket
                    val timestamp = (System.currentTimeMillis() / 1000) * 1000

                    saveInternal(
                        sender = remoteMsg.sender,
                        receiver = remoteMsg.receiver,
                        text = remoteMsg.message,
                        sessionEmail = myEmail,
                        timestamp = timestamp
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun sendMessage(userEmail: String, text: String, fromUser: Boolean) {
        val myEmail = sessionManager.userEmail()
        val now = (System.currentTimeMillis() / 1000) * 1000 // Normalizado

        val chatMsg = ChatMessage(sender = myEmail, receiver = userEmail, message = text)
        chatService.sendMessage(gson.toJson(chatMsg))

        saveInternal(
            sender = myEmail,
            receiver = userEmail,
            text = text,
            sessionEmail = myEmail,
            timestamp = now
        )
    }

    private suspend fun saveInternal(
        sender: String,
        receiver: String,
        text: String,
        sessionEmail: String,
        timestamp: Long
    ) {
        val entity = MessageEntity(
            senderEmail = sender,
            receiverEmail = receiver,
            text = text.trim(),
            timestamp = timestamp,
            userEmail = sessionEmail
        )
        // El DAO debe tener OnConflictStrategy.IGNORE para que esto no duplique
        messageDao.insert(entity)
    }

    override suspend fun deleteAll(userEmail: String) {
        messageDao.deleteAll(userEmail)
    }

    override fun observeLastMessages(): Flow<List<MessageEntity>> {
        val myEmail = sessionManager.userEmail()
        return messageDao.observeLastMessages(myEmail)
    }
}