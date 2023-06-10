package com.bangkit.ecoeasemitra.data.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import com.bangkit.ecoeasemitra.data.repository.MainRepository
import com.bangkit.ecoeasemitra.helper.getLastLocation
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel(private val repository: MainRepository) : ViewModel() {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(repository.context)
    private val _lastLocationStateFlow: MutableStateFlow<UiState<Location>> = MutableStateFlow(UiState.Loading)
    val lastLocationStateFlow: StateFlow<UiState<Location>> = _lastLocationStateFlow

    fun getLastLocation(){
        try {
            fusedLocationClient.getLastLocation(
                context = repository.context,
                onSuccess = { location ->
                    _lastLocationStateFlow.value = UiState.Success(location)
                },
                onError = { error ->
                    _lastLocationStateFlow.value = UiState.Error("error: $error")
                }
            )
        }catch (e: Exception){
            Log.d("TAG", "getLastLocation: $e")
            _lastLocationStateFlow.value = UiState.Error("error: ${e.message}")
        }
    }

    fun reloadLastLocation(){
        _lastLocationStateFlow.value = UiState.Loading
    }
}