package com.example.consolas.ui.viewmodels

import androidx.lifecycle.*
import com.example.consolas.data.local.SessionManager
import com.example.consolas.domain.useCase.*
import com.example.consolas.domain.model.*
import com.example.consolas.domain.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val consoles: LiveData<List<Console>> = localRepository.observeAllConsoles().asLiveData()

    init { refreshFromApi() }

    fun refreshFromApi() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Pedimos a la API
                val apiResult = getConsolesUseCase()
                // 2. Guardamos en Local (Él se encarga de no duplicar)
                localRepository.upsertConsoles(apiResult)
            } catch (e: Exception) {
                android.util.Log.e("VM", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun getCurrentUserEmail(): String {
        return sessionManager.userEmail()
    }

    fun editConsole(oldName: String, updateConsole: UpdateConsole) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                editConsoleUseCase(oldName, updateConsole)
                val email = sessionManager.userEmail()
                if (updateConsole.name != null && updateConsole.name != oldName) {
                    localRepository.deleteLocalConsole(email, oldName)
                }
                refreshFromApi()
            } catch (e: Exception) {
                android.util.Log.e("EDIT", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addConsole(console: Console) {
        viewModelScope.launch {
            addConsoleUseCase(console)
            refreshFromApi()
        }
    }

    fun deleteConsole(name: String) {
        viewModelScope.launch {
            deleteConsoleUseCase(name)
            localRepository.deleteLocalConsole(sessionManager.userEmail(), name)
        }
    }

    fun setFavorite(name: String, favorite: Boolean) {
        viewModelScope.launch {
            localRepository.setFavorite(sessionManager.userEmail(), name, favorite)
        }
    }

    fun addGameToConsole(consoleName: String, game: Game, isNative: Boolean) {
        viewModelScope.launch {
            localRepository.addGameToConsole(consoleName, game, isNative, sessionManager.userEmail())
        }
    }
}