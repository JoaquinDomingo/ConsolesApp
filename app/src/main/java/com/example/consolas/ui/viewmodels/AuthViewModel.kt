package com.example.consolas.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consolas.domain.useCase.LoginUseCase
import com.example.consolas.domain.useCase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _authState = MutableLiveData<Result<Boolean>?>()
    val authState: LiveData<Result<Boolean>?> = _authState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, pass: String, name: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val result = loginUseCase(email, pass, name)
            _authState.value = result
            _isLoading.value = false
        }
    }

    fun register(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = registerUseCase(email, pass)
            _authState.value = result
            _isLoading.value = false
        }
    }


    fun clearState() {
        _authState.value = null
    }
}