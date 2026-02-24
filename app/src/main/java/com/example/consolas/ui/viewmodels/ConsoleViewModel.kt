package com.example.consolas.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consolas.data.local.SessionManager
import com.example.consolas.domain.useCase.*
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.UpdateConsole
import com.example.consolas.domain.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
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

    private val _consoles = MutableLiveData<List<Console>>()
    val consoles: LiveData<List<Console>> = _consoles

    private var observeJob: Job? = null

    val favoriteConsoles = localRepository
        .observeFavoriteConsoles(sessionManager.userEmail())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        loadConsoles()
    }

    fun loadConsoles() {
        val email = sessionManager.userEmail()
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            try {
                val apiResult = getConsolesUseCase()
                if (apiResult.isNotEmpty()) {
                    if (email.isNotBlank()) {
                        localRepository.upsertConsoles(email, apiResult)
                    }
                    // Mantenemos los datos de la API como prioridad para no perder las listas de juegos
                    _consoles.value = apiResult
                }
            } catch (e: Exception) {
                // Si falla la red, usamos Room como respaldo
                if (email.isNotBlank()) {
                    localRepository.observeLocalConsoles(email).collect { listaRoom ->
                        if (listaRoom.isNotEmpty()) _consoles.value = listaRoom
                    }
                }
            }
        }
    }

    fun setFavorite(name: String, favorite: Boolean) {
        viewModelScope.launch {
            val email = sessionManager.userEmail()
            if (email.isNotBlank()) {
                localRepository.setFavorite(email, name, favorite)

                // CORRECCIÓN: Usamos .let o una comprobación de nulabilidad segura
                _consoles.value?.let { currentList ->
                    val updatedList = currentList.map {
                        if (it.name == name) it.copy(favorite = favorite) else it
                    }
                    _consoles.value = updatedList
                }
            }
        }
    }

    fun editConsole(oldName: String, updateConsole: UpdateConsole) {
        viewModelScope.launch {
            editConsoleUseCase(oldName, updateConsole)
            val email = sessionManager.userEmail()
            // SOLUCIÓN: Usamos el nombre actualizado o, si es null, mantenemos el antiguo
            val newName = updateConsole.name ?: oldName

            if (email.isNotBlank() && newName != oldName) {
                localRepository.deleteLocalConsole(email, oldName)
            }
            loadConsoles()
        }
    }

    fun addConsole(console: Console) {
        viewModelScope.launch {
            addConsoleUseCase(console)
            loadConsoles()
        }
    }

    fun deleteConsole(name: String) {
        viewModelScope.launch {
            deleteConsoleUseCase(name)
            val email = sessionManager.userEmail()
            if (email.isNotBlank()) {
                localRepository.deleteLocalConsole(email, name)
            }
            loadConsoles()
        }
    }
}