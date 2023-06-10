package com.bangkit.ecoeasemitra.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoeasemitra.data.model.ImageCaptured
import com.bangkit.ecoeasemitra.data.repository.MainRepository
import com.bangkit.ecoeasemitra.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class CameraViewModel(private val repository: MainRepository): ViewModel() {
    private val _uiStateImageCaptured: MutableStateFlow<UiState<ImageCaptured>> = MutableStateFlow(UiState.Loading)
    val uiStateImageCaptured: StateFlow<UiState<ImageCaptured>> = _uiStateImageCaptured

    fun setImage(imageCaptured: ImageCaptured){
        Log.d("TAG", "setImage: $imageCaptured")
        _uiStateImageCaptured.value = UiState.Loading
        repository.setCapturedImage(imageCaptured)
    }

    fun getImageUri(){
        viewModelScope.launch {
            try {
                repository.getCapturedImage()
                    .catch { error ->
                        Log.d("TAG", "getImageUri: ${error.message}")
                        _uiStateImageCaptured.value = UiState.Error(error.message.toString())
                    }
                    .collect{imageCaptured ->
                        _uiStateImageCaptured.value = UiState.Success(imageCaptured)
                        Log.d("TAG", "getImageCaptured success: ${imageCaptured.uri}")
                    }
            }catch (e: Exception){
                _uiStateImageCaptured.value = UiState.Error(e.message.toString())
                Log.d("TAG", "getImageUri: ${e.message}")
            }
        }
    }
}