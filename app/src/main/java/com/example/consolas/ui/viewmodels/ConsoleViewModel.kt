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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsoleViewModel @Inject constructor(
    private val getConsolesUseCase: GetConsoleUseCase,
    private val addConsoleUseCase: AddNewConsoleUseCase,
    private val deleteConsoleUseCase: DeleteConsoleUseCase,
    private val editConsoleUseCase: EditConsoleUseCase
) : ViewModel() {

    private val _consoles = MutableLiveData<List<Console>>()
    val consoles: LiveData<List<Console>> = _consoles

    init { loadConsoles() }

    fun loadConsoles() {
        viewModelScope.launch {
            val result = getConsolesUseCase()
            _consoles.value = result
        }
    }

    fun addConsole(console: Console) {
        viewModelScope.launch {
            addConsoleUseCase(console)
            loadConsoles()
        }
    }

    fun deleteConsole(console: Console) {
        viewModelScope.launch {
            deleteConsoleUseCase(console)
            loadConsoles()
        }
    }

    fun editConsole(position: Int, console: Console) {
        viewModelScope.launch {
            editConsoleUseCase(position, console)
            loadConsoles()
        }
    }
}