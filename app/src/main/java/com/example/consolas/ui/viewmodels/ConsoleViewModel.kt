package com.example.consolas.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consolas.domain.useCase.AddNewConsoleUseCase
import com.example.consolas.domain.useCase.DeleteConsoleUseCase
import com.example.consolas.domain.useCase.EditConsoleUseCase
import com.example.consolas.domain.useCase.GetConsoleUseCase
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.UpdateConsole // Asegúrate de tener este import
import dagger.hilt.android.lifecycle.HiltViewModel

import com.example.consolas.domain.repository.LocalRepository
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

    val favoriteConsoles = localRepository
        .observeFavoriteConsoles(sessionManager.userEmail())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        loadConsoles()
    }

    fun loadConsoles() {
        viewModelScope.launch {
            val result = getConsolesUseCase()
            _consoles.value = result

            //Cache en Room por cada usuario
            val email = sessionManager.userEmail()
            if (email.isNotBlank()) {
                localRepository.upsertConsoles(email, result)
            }
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
            loadConsoles()
        }
    }


    fun editConsole(oldName: String, updateConsole: UpdateConsole) {
        viewModelScope.launch {
            editConsoleUseCase(oldName, updateConsole)

            val newName = updateConsole.name
            val email = sessionManager.userEmail()
            if (email.isNotBlank() && !newName.isNullOrBlank() && newName != oldName) {
                localRepository.deleteLocalConsole(email, oldName)
            }

            loadConsoles()
        }
    }

    fun setFavorite(name: String, favorite: Boolean) {
        viewModelScope.launch {
            val email = sessionManager.userEmail()
            if (email.isNotBlank()) {
                localRepository.setFavorite(email, name, favorite)
            }
        }
    }
}