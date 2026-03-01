package com.example.consolas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consolas.data.local.MessageEntity
import com.example.consolas.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val repository: MessageRepository
) : ViewModel() {


    val conversations: StateFlow<List<MessageEntity>> = repository.observeLastMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Opcional: Función para borrar una conversación completa si lo deseas
    fun deleteChat(email: String) {
        viewModelScope.launch {
            repository.deleteAll(email)
        }
    }
}