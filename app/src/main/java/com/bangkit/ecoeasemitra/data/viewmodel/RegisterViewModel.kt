package com.bangkit.ecoeasemitra.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoeasemitra.data.event.MyEvent
import com.bangkit.ecoeasemitra.data.model.ImageCaptured
import com.bangkit.ecoeasemitra.data.model.request.Register
import com.bangkit.ecoeasemitra.data.repository.MainRepository
import com.bangkit.ecoeasemitra.helper.InputValidation
import com.bangkit.ecoeasemitra.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class RegisterViewModel(private val repository: MainRepository) : ViewModel() {
    private val eventChannel = Channel<MyEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    val firstnameValidation: InputValidation = InputValidation("",false, "")
    val lastnameValidation: InputValidation = InputValidation("",false, "")
    val phoneNumberValidation: InputValidation = InputValidation("",false, "")
    val emailValidation: InputValidation = InputValidation("",false, "")
    val passwordValidation: InputValidation = InputValidation("",false, "")

    private val _uiStateProfileImage: MutableStateFlow<UiState<ImageCaptured>> = MutableStateFlow(UiState.Loading)
    val uiStateProfileImage: StateFlow<UiState<ImageCaptured>> = _uiStateProfileImage

    fun setProfileImage(imageCaptured: ImageCaptured){
        _uiStateProfileImage.value = UiState.Loading
        repository.setCapturedImage(imageCaptured)
    }
    fun getProfileImageUri(){
        viewModelScope.launch {
            try {
                repository.getCapturedImage()
                    .catch { error ->
                        Log.d("TAG", "getImageUri: ${error.message}")
                        _uiStateProfileImage.value = UiState.Error(error.message.toString())
                    }
                    .collect{imageCaptured ->
                        _uiStateProfileImage.value = UiState.Success(imageCaptured)
                        Log.d("TAG", "getImageCaptured success: ${imageCaptured.uri}")
                    }
            }catch (e: Exception){
                _uiStateProfileImage.value = UiState.Error(e.message.toString())
                Log.d("TAG", "getImageUri: ${e.message}")
            }
        }
    }
    fun validateEmailInput(){
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        emailValidation.setErrorMessage(
            when {
                emailValidation.inputValue.value.isEmpty() -> "Email harus diisi!"
                !emailValidation.inputValue.value.matches(emailRegex.toRegex()) -> "Format email salah!"
                else -> ""
            }
        )
    }
    fun validatePasswordInput(){
        passwordValidation.setErrorMessage(
            when {
                passwordValidation.inputValue.value.isEmpty() -> "Password harus diisi!"
                passwordValidation.inputValue.value.length < 8 -> "Password minimal harus 8 karakter!"
                else -> ""
            }
        )
    }
    fun validatePhoneNumberInput(){
        phoneNumberValidation.setErrorMessage(
            when {
                phoneNumberValidation.inputValue.value.isEmpty() -> "Nomor telepon harus diisi!"
                phoneNumberValidation.inputValue.value.length < 11 -> "Nomor telepon minimal harus 11 karakter!"
                phoneNumberValidation.inputValue.value.length > 13 -> "Nomor telepon maximal harus 13 karakter!"
                else -> ""
            }
        )
    }
    fun validateFirstnameInput(){
        firstnameValidation.setErrorMessage(
            when {
                firstnameValidation.inputValue.value.isEmpty() -> "Nama  harus diisi!"
                else -> ""
            }
        )
    }
    fun validateLastnameInput(){
        lastnameValidation.setErrorMessage(
            when {
                firstnameValidation.inputValue.value.isEmpty() -> "Nama  harus diisi!"
                else -> ""
            }
        )
    }

    private fun resetAllInput(){
        firstnameValidation.updateInputValue("")
        lastnameValidation.updateInputValue("")
        phoneNumberValidation.updateInputValue("")
        emailValidation.updateInputValue("")
        passwordValidation.updateInputValue("")
        _uiStateProfileImage.value = UiState.Loading
    }
    fun register(photoProfileFile: File, onSuccess: () -> Unit){
        run{
            validateFirstnameInput()
            validateLastnameInput()
            validatePhoneNumberInput()
            validateEmailInput()
            validatePasswordInput()
        }
        val isAllInputValid = listOf(firstnameValidation, emailValidation, phoneNumberValidation, passwordValidation).all { !it.isErrorState.value }
        if(isAllInputValid){
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val fileRequestBody = photoProfileFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val fileFieldName = "photoFile"
                    val filePart = MultipartBody.Part.createFormData(
                        fileFieldName,
                        photoProfileFile.name,
                        fileRequestBody
                    )
                    val registerData = Register(
                        photoFile = filePart,
                        firstName = firstnameValidation.inputValue.value.toRequestBody("text/plain".toMediaType()),
                        lastName = lastnameValidation.inputValue.value.toRequestBody("text/plain".toMediaType()),
                        email = emailValidation.inputValue.value.toRequestBody("text/plain".toMediaType()),
                        phone_number = phoneNumberValidation.inputValue.value.toRequestBody("text/plain".toMediaType()),
                        password = passwordValidation.inputValue.value.toRequestBody("text/plain".toMediaType()),
                    )
                    repository.registerUser(registerData).catch { error ->
                        eventChannel.send(MyEvent.MessageEvent("error: ${error.message}"))
                    }.collect{result ->
                        resetAllInput()
                        withContext(Dispatchers.Main){ onSuccess() }
                    }
                }catch (e: Exception){
                    eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
                }
            }
        }
    }
}