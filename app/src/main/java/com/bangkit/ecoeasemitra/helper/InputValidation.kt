package com.bangkit.ecoeasemitra.helper

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InputValidation(
    private var initialInputValue: String,
    private var initialErrorValue: Boolean,
    private var errorMessage: String,
){
    private var _inputValue: MutableStateFlow<String> = MutableStateFlow(initialInputValue)
    private var _isErrorState: MutableStateFlow<Boolean> = MutableStateFlow(initialErrorValue)
    val inputValue: StateFlow<String> = _inputValue
    val isErrorState: StateFlow<Boolean> = _isErrorState

    fun setErrorMessage(message: String){
        errorMessage = message
        _isErrorState.value = if (message.isEmpty()) false else true
    }

    fun updateInputValue(value: String){
        _inputValue.value = value
    }

    fun getErrorMessage(): String = errorMessage
}
