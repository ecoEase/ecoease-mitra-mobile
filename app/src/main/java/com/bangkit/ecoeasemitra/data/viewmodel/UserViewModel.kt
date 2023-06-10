package com.bangkit.ecoeasemitra.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoeasemitra.data.repository.MainRepository
import com.bangkit.ecoeasemitra.data.room.model.User
import com.bangkit.ecoeasemitra.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UserViewModel(private val repository: MainRepository): ViewModel() {
    private var _user: MutableStateFlow<UiState<User>> = MutableStateFlow(UiState.Loading)
    val user: StateFlow<UiState<User>> = _user

    fun getUser(){
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repository.getUser().catch { error ->
                    _user.value = UiState.Error("error: ${error.message}")
                }.collect{
                    _user.value = if(it == null) UiState.Error("data null") else UiState.Success(it)
                }
            }
        }catch (e: Exception){
            _user.value = UiState.Error("error: ${e.message}")
        }
    }

    fun reloadUser(){ _user.value = UiState.Loading }
}