package com.example.consolas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consolas.data.local.SessionManager
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

    private val email = sessionManager.userEmail()

    val messages: StateFlow<List<Message>> = messageRepository
        .observeMessages(email)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun send(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            messageRepository.sendMessage(email, text, fromUser = true)

            // Respuesta "mock" para la demo en clase (hasta que haya chat real)
            messageRepository.sendMessage(email, "Recibido ✅ (demo)", fromUser = false)
        }
    }

    fun clear() {
        viewModelScope.launch {
            messageRepository.deleteAll(email)
        }
    }
}
