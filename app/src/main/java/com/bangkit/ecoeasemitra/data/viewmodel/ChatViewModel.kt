package com.bangkit.ecoeasemitra.data.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit.ecoeasemitra.data.repository.MainRepository
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel(private val repository: MainRepository) : ViewModel() {
    private val _chatRoomsState: MutableStateFlow<UiState<List<String>>> = MutableStateFlow(UiState.Loading)

    val chatRoomState: StateFlow<UiState<List<String>>> = _chatRoomsState

    fun getChatRoom(referenceTask: Task<List<String>>){

    }
}