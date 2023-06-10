package com.bangkit.ecoeasemitra.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoeasemitra.data.event.MyEvent
import com.bangkit.ecoeasemitra.data.model.request.Login
import com.bangkit.ecoeasemitra.data.repository.MainRepository
import com.bangkit.ecoeasemitra.helper.InputValidation
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(private val repository: MainRepository) : ViewModel() {
    val emailValidation: InputValidation = InputValidation("", false, "")
    val passwordValidation: InputValidation = InputValidation("", false, "")
    private var _isLoginValid: MutableStateFlow<UiState<Boolean>> =
        MutableStateFlow(UiState.Success(false))
    val isLoginValid: StateFlow<UiState<Boolean>> = _isLoginValid

    private val eventChannel = Channel<MyEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    fun validateEmailInput() {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        emailValidation.setErrorMessage(
            when {
                emailValidation.inputValue.value.isEmpty() -> "Email harus diisi!"
                !emailValidation.inputValue.value.matches(emailRegex.toRegex()) -> "Format email salah!"
                else -> ""
            }
        )
    }

    fun validatePasswordInput() {
        passwordValidation.setErrorMessage(
            when {
                passwordValidation.inputValue.value.isEmpty() -> "Password harus diisi!"
                passwordValidation.inputValue.value.length < 8 -> "Password minimal harus 8 karakter!"
                else -> ""
            }
        )
    }

    private fun resetAllInputValue() {
        emailValidation.updateInputValue("")
        passwordValidation.updateInputValue("")
    }

    fun login(onSuccess: () -> Unit) {
        run {
            validateEmailInput()
            validatePasswordInput()
        }
        val isAllInputValid =
            listOf(emailValidation, passwordValidation).all { !it.isErrorState.value }
        if (isAllInputValid) {
            _isLoginValid.value = UiState.Loading
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    repository.loginUser(
                        Login(
                            email = emailValidation.inputValue.value,
                            password = passwordValidation.inputValue.value
                        )
                    ).catch { error ->
                        _isLoginValid.value = UiState.Error("error: ${error.message}")
                    }.collect {
                        _isLoginValid.value = UiState.Success(true)
                        setFCMToken(it.id)
                        resetAllInputValue()
                        withContext(Dispatchers.Main) { onSuccess() }
                    }
                } catch (e: Exception) {
                    _isLoginValid.value = UiState.Error("error: ${e.message}")
                }
            }
        }

    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.logout()
                resetFCMToken()
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }

    private fun setFCMToken(id: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            viewModelScope.launch(Dispatchers.IO) {
                repository.setFCMToken(id = id, token = it.result)
            }
        }
    }

    private fun resetFCMToken() {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
            viewModelScope.launch(Dispatchers.IO) {
                repository.setFCMToken(token = "")
            }
        }
    }
}