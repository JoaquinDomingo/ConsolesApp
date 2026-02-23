package com.example.consolas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consolas.data.local.SessionManager
import com.example.consolas.domain.repository.LocalRepository
import com.example.consolas.domain.repository.UserStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val userName: String = sessionManager.userName()
    val userEmail: String = sessionManager.userEmail()

    private val _stats = MutableStateFlow(UserStats(0, 0.0))
    val stats: StateFlow<UserStats> = _stats

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _stats.value = localRepository.getUserStats(userEmail)
        }
    }
}
