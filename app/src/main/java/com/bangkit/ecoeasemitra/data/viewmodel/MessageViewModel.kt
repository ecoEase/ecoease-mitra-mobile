package com.bangkit.ecoeasemitra.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoeasemitra.data.event.MyEvent
import com.bangkit.ecoeasemitra.data.model.request.FCMNotification
import com.bangkit.ecoeasemitra.data.repository.MainRepository
import com.bangkit.ecoeasemitra.data.room.model.User
import com.bangkit.ecoeasemitra.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageViewModel(private val repository: MainRepository) : ViewModel() {
    private val eventChannel = Channel<MyEvent>()
    val eventFlow = eventChannel.receiveAsFlow()
    private var _user: MutableStateFlow<UiState<User>> = MutableStateFlow(UiState.Loading)
    val user: StateFlow<UiState<User>> = _user

    fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                delay(200)
                repository.getUser().catch {
                    _user.value = UiState.Error("error: ${it.message}")
                    eventChannel.send(MyEvent.MessageEvent("error: ${it.message}"))
                }.collect {
                    _user.value = UiState.Success(it)
                }
            } catch (e: Exception) {
                _user.value = UiState.Error("error: ${e.message}")
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }

    fun reloadCurrentUser() {
        _user.value = UiState.Loading
    }

    private fun subscribeToChatroom() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                _user = repository.getUser()
                //get chatrooms
            } catch (e: Exception) {
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }

    fun createChatroom() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.createChatroom().catch {
                    eventChannel.send(MyEvent.MessageEvent("error: ${it.message}"))
                }.collect {
                    eventChannel.send(MyEvent.MessageEvent("success creating room ${it.data?.id}"))
                }
            } catch (e: Exception) {
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }

    fun deleteChatroom(roomKey: String, roomId: String, onSuccess: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteChatroom(roomKey, roomId).catch {error ->
                    eventChannel.send(MyEvent.MessageEvent("error: ${error.message}"))
                }.collect{
                    eventChannel.send(MyEvent.MessageEvent("success delete chat room"))
                    withContext(Dispatchers.IO){
                        onSuccess()
                    }
                }
            }catch (e: Exception){
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }

    fun getChatrooms(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getChatRooms().catch {error ->
                    eventChannel.send(MyEvent.MessageEvent("error: ${error.message}"))
                }.collect{
                    eventChannel.send(MyEvent.MessageEvent("success delete chat room"))
                }
            }catch (e: Exception){
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }

    fun sendNotification(bodyMessage: FCMNotification) {
        viewModelScope.launch {
            try {
                repository.sendNotification(bodyMessage)
            } catch (e: Exception) {
                Log.d("TAG", "sendNotifsendNotif: ${e.message}")
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }
}