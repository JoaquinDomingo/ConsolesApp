package com.example.consolas.ui.viewmodels

import androidx.lifecycle.*
import com.example.consolas.data.local.SessionManager
import com.example.consolas.domain.useCase.*
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.UpdateConsole
import com.example.consolas.domain.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsoleViewModel @Inject constructor(
    private val getConsolesUseCase: GetConsoleUseCase,
    private val addConsoleUseCase: AddNewConsoleUseCase,
    private val deleteConsoleUseCase: DeleteConsoleUseCase,
    private val editConsoleUseCase: EditConsoleUseCase,
    private val localRepository: LocalRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val userEmailState = MutableStateFlow(sessionManager.userEmail())

    val consoles: LiveData<List<Console>> = userEmailState.flatMapLatest { email ->
        localRepository.observeLocalConsoles(email)
    }.asLiveData()

    init {
        refreshFromApi()
    }

    fun refreshFromApi() {
        val email = sessionManager.userEmail()
        if (email.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val apiResult = getConsolesUseCase()
                if (apiResult.isNotEmpty()) {
                    localRepository.upsertConsoles(email, apiResult)
                }
            } catch (e: Exception) {
                android.util.Log.e("DEBUG_API", "Fallo de red: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addConsole(console: Console) {
        viewModelScope.launch {
            _isLoading.value = true
            addConsoleUseCase(console)
            _isLoading.value = false
        }
    }

    fun deleteConsole(name: String) {
        viewModelScope.launch {
            deleteConsoleUseCase(name)
            val email = sessionManager.userEmail()
            localRepository.deleteLocalConsole(email, name)
        }
    }

    fun editConsole(oldName: String, updateConsole: UpdateConsole) {
        viewModelScope.launch {
            _isLoading.value = true
            editConsoleUseCase(oldName, updateConsole)
            val email = sessionManager.userEmail()
            if (updateConsole.name != null && updateConsole.name != oldName) {
                localRepository.deleteLocalConsole(email, oldName)
            }
            refreshFromApi()
        }
    }

    fun setFavorite(name: String, favorite: Boolean) {
        viewModelScope.launch {
            localRepository.setFavorite(sessionManager.userEmail(), name, favorite)
        }
    }
}