package com.bangkit.ecoeasemitra.data.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoeasemitra.data.repository.MainRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(private val repository: MainRepository): ViewModel() {
    private val _isLoading = mutableStateOf(true)
    private val _isReadOnboard = MutableStateFlow(false)
    private val _isLogged = MutableStateFlow(false)
    val isLoading: State<Boolean> = _isLoading
    val isReadOnboard: StateFlow<Boolean> = _isReadOnboard
    val isLogged: StateFlow<Boolean> = _isLogged

    init {
        viewModelScope.launch {
            _isReadOnboard.value = repository.getIsFinishOnboard()
            _isLogged.value = repository.getToken().isNotEmpty()
            //adding delay so when splash screen is finish the determine screen directly show
            delay(4000)
            _isLoading.value = false
        }
    }
    fun finishedOnBoard(){
        viewModelScope.launch {
            repository.finishOnBoard()
        }
    }
}