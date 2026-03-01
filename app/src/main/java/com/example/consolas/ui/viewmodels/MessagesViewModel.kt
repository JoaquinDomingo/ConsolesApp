package com.example.consolas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consolas.data.local.SessionManager
import com.example.consolas.data.repository.MessageRepositoryImpl
import com.example.consolas.domain.repository.Message
import com.example.consolas.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val myEmail = sessionManager.userEmail()
    private var currentOtherEmail: String = ""

    // Observamos los mensajes filtrados por el email del contacto con el que hablamos
    lateinit var messages: StateFlow<List<Message>>

    // Dentro de MessagesViewModel
    fun getMyEmail(): String {
        return sessionManager.userEmail() // sessionManager debe estar inyectado en el constructor
    }
    /**
     * Se llama desde el Fragment al entrar, pasando el email del contacto
     */
    fun initChat(otherEmail: String) {
        this.currentOtherEmail = otherEmail

        // 1. Inicializamos el Flow observando solo los mensajes de este contacto
        messages = messageRepository
            .observeMessages(otherEmail)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        // 2. Conectamos el WebSocket (si no está conectado ya)
        // Casteamos a la implementación para acceder a los métodos de red
        (messageRepository as? MessageRepositoryImpl)?.startRealtimeChat(myEmail)

        // 3. Sincronizamos historial antiguo desde MariaDB
        viewModelScope.launch {
            (messageRepository as? MessageRepositoryImpl)?.syncWithServer(otherEmail)
        }
    }

    fun send(text: String) {
        if (text.isBlank() || currentOtherEmail.isBlank()) return

        viewModelScope.launch {
            // Enviamos el mensaje real al servidor y lo guardamos en Room
            messageRepository.sendMessage(currentOtherEmail, text, fromUser = true)
        }
    }

    fun clear() {
        viewModelScope.launch {
            if (currentOtherEmail.isNotBlank()) {
                messageRepository.deleteAll(currentOtherEmail)
            }
        }
    }
}