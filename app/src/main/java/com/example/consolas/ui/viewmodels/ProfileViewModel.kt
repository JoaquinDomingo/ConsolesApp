package com.example.consolas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consolas.data.local.SessionManager
import com.example.consolas.domain.repository.LocalRepository
import com.example.consolas.domain.repository.UserStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val userEmail: String = sessionManager.userEmail()
    val userName: String = sessionManager.userName()

    private val _stats = MutableStateFlow(UserStats(0, 0.0))
    val stats: StateFlow<UserStats> = _stats.asStateFlow()

    init {
        refreshStats()
    }

    fun refreshStats() {
        viewModelScope.launch {
            if (userEmail.isNotBlank()) {
                val data = localRepository.getUserStats(userEmail)
                _stats.value = data
            }
        }
    }
}