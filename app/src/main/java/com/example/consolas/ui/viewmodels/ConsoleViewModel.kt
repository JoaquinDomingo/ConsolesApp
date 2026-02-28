package com.example.consolas.ui.viewmodels

import androidx.lifecycle.*
import com.example.consolas.data.local.SessionManager
import com.example.consolas.domain.useCase.*
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.Game
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
            if (consoles.value.isNullOrEmpty()) _isLoading.value = true

            try {
                val apiResult = getConsolesUseCase()
                localRepository.upsertConsoles(email, apiResult)
            } catch (e: Exception) {
                android.util.Log.e("DEBUG_API", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun addGameToConsole(consoleName: String, game: Game, isNative: Boolean) {
        val email = sessionManager.userEmail()

        viewModelScope.launch {
            _isLoading.value = true
            val success = localRepository.addGameToConsole(consoleName, game, isNative, email)

            if (success) {
                android.util.Log.d("DEBUG_VM", "Actualización completada para $email")
            }
            _isLoading.value = false
        }
    }

    fun addConsole(console: Console) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addConsoleUseCase(console)
                refreshFromApi()
            } catch (e: Exception) {
                android.util.Log.e("DEBUG_ADD", "Error al añadir consola: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteConsole(name: String) {
        viewModelScope.launch {
            try {
                deleteConsoleUseCase(name)
                val email = sessionManager.userEmail()
                localRepository.deleteLocalConsole(email, name)
            } catch (e: Exception) {
                android.util.Log.e("DEBUG_DELETE", "Error al borrar consola: ${e.message}")
            }
        }
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
                android.util.Log.e("DEBUG_EDIT", "Error al editar consola: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setFavorite(name: String, favorite: Boolean) {
        viewModelScope.launch {
            localRepository.setFavorite(sessionManager.userEmail(), name, favorite)
        }
    }

    fun updateEmail() {
        userEmailState.value = sessionManager.userEmail()
    }
}