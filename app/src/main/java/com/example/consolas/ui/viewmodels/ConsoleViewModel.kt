package com.example.consolas.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consolas.data.local.SessionManager
import com.example.consolas.domain.useCase.AddNewConsoleUseCase
import com.example.consolas.domain.useCase.DeleteConsoleUseCase
import com.example.consolas.domain.useCase.EditConsoleUseCase
import com.example.consolas.domain.useCase.GetConsoleUseCase
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

    // En ConsoleViewModel.kt
    fun loadConsoles() {
        val email = sessionManager.userEmail()
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            try {
                // 1. Traemos los datos COMPLETOS de la API (con sus juegos)
                val apiResult = getConsolesUseCase()

                if (email.isNotBlank()) {
                    // 2. Guardamos en Room solo para favoritos
                    localRepository.upsertConsoles(email, apiResult)

                    // 3. ¡IMPORTANTE! Asignamos el resultado de la API directamente.
                    // Así, los objetos Console mantienen sus listas de juegos intactas.
                    _consoles.value = apiResult
                }
            } catch (e: Exception) {
                // Solo si no hay internet usamos Room como plan B
                localRepository.observeLocalConsoles(email).collect { listaRoom ->
                    _consoles.value = listaRoom
                }
            }
        }
    }

    fun setFavorite(name: String, favorite: Boolean) {
        viewModelScope.launch {
            val email = sessionManager.userEmail()
            if (email.isNotBlank()) {
                localRepository.setFavorite(email, name, favorite)
                val updatedList = _consoles.value?.map {
                    if (it.name == name) it.copy(favorite = favorite) else it
                }
                _consoles.value = updatedList
            }
        }
    }

    fun addConsole(console: Console) {
        viewModelScope.launch {
            addConsoleUseCase(console)
        }
    }

    fun deleteConsole(name: String) {
        viewModelScope.launch {
            val email = sessionManager.userEmail()
            deleteConsoleUseCase(name)
            if (email.isNotBlank()) {
                localRepository.deleteLocalConsole(email, name)
            }
        }
    }

    fun editConsole(oldName: String, updateConsole: UpdateConsole) {
        viewModelScope.launch {
            editConsoleUseCase(oldName, updateConsole)
            val email = sessionManager.userEmail()
            // CORRECCIÓN: Usamos el operador elvis ?: "" para asegurar que no sea null
            val newName = updateConsole.name ?: ""

            if (email.isNotBlank() && newName.isNotBlank() && newName != oldName) {
                localRepository.deleteLocalConsole(email, oldName)
            }
            loadConsoles()
        }
    }
}