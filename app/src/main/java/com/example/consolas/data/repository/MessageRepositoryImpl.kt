package com.example.consolas.data.repository

import com.example.consolas.data.local.MessageDao
import com.example.consolas.data.local.MessageEntity
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
    private val gson: Gson
) : MessageRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun observeMessages(userEmail: String): Flow<List<Message>> =
        messageDao.observeMessages(userEmail).map { list ->
            list.map { Message(it.id, it.text, it.timestamp, it.fromUser) }
        }

    // Implementación obligatoria de la interfaz
    override fun startRealtimeChat(myEmail: String) {
        // CORRECCIÓN: connect ya no pide el email, lo saca del SessionManager internamente
        chatService.connect { json ->
            try {
                val chatMsg = gson.fromJson(json, ChatMessage::class.java)
                repositoryScope.launch {
                    // CORRECCIÓN: Si recibimos un mensaje, el 'userEmail' de la conversación
                    // para Room es el 'sender' (el que nos lo envió).
                    saveInternal(
                        userEmail = chatMsg.sender,
                        text = chatMsg.message,
                        fromUser = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Implementación obligatoria de la interfaz
    override suspend fun syncWithServer(otherUserEmail: String) {
        try {
            val response = api.getChatHistory(otherUserEmail)
            if (response.isSuccessful) {
                response.body()?.forEach { remoteMsg ->
                    // Guardamos en Room lo que bajamos de la base de datos MariaDB
                    saveInternal(
                        userEmail = otherUserEmail,
                        text = remoteMsg.message,
                        // Si el sender NO es el otro usuario, significa que lo envié yo
                        fromUser = remoteMsg.sender != otherUserEmail
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun sendMessage(userEmail: String, text: String, fromUser: Boolean) {
        // 1. Enviamos al servidor vía WebSocket (Ktor)
        // NOTA: Asegúrate de que el sender sea tu email real guardado en SessionManager
        val chatMsg = ChatMessage(
            sender = "tu_email_actual@gmail.com",
            receiver = userEmail,
            message = text
        )
        chatService.sendMessage(gson.toJson(chatMsg))

        // 2. Guardamos en la base de datos local (Room)
        saveInternal(userEmail, text, fromUser)
    }

    private suspend fun saveInternal(userEmail: String, text: String, fromUser: Boolean) {
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

    override fun observeLastMessages(): Flow<List<MessageEntity>> {
        return messageDao.observeLastMessages()
    }
}