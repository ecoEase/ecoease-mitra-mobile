package com.bangkit.ecoeasemitra.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.ecoeasemitra.data.event.MyEvent
import com.bangkit.ecoeasemitra.data.model.request.FCMNotification
import com.bangkit.ecoeasemitra.data.model.request.Notification
import com.bangkit.ecoeasemitra.data.remote.responseModel.chatroom.ChatRoomItem
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
    private var _chatrooms: MutableStateFlow<UiState<List<ChatRoomItem>>> = MutableStateFlow(UiState.Loading)
    private var _detailChatroom: MutableStateFlow<UiState<ChatRoomItem>> = MutableStateFlow(UiState.Loading)

    val user: StateFlow<UiState<User>> = _user
    val chatrooms: StateFlow<UiState<List<ChatRoomItem>>> = _chatrooms
    val detailChatrooms: StateFlow<UiState<ChatRoomItem>> = _detailChatroom

    // TODO: add on reload chatrooms 
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

    fun createChatroom(targetUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.createChatroom(userId = targetUserId).catch {
                    eventChannel.send(MyEvent.MessageEvent("error: ${it.message}"))
                }.collect {
                    eventChannel.send(MyEvent.MessageEvent("success creating room ${it.data?.id}"))
                }
            } catch (e: Exception) {
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }

    fun deleteChatroom(roomKey: String, roomId: String) {
        Log.d("TAG", "deleteChatroom: $roomKey $roomId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteChatroom(roomKey, roomId).catch { error ->
                    eventChannel.send(MyEvent.MessageEvent("error: ${error.message}"))
                }.collect {
                    eventChannel.send(MyEvent.MessageEvent("success delete chat room"))
                    _chatrooms.value = UiState.Loading
                }
            } catch (e: Exception) {
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }

    fun getChatrooms() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(200)
            try {
                repository.getChatRooms().catch { error ->
                    _chatrooms.value = UiState.Error("error: ${error.message}")
                    eventChannel.send(MyEvent.MessageEvent("error: ${error.message}"))
                }.collect {
                    _chatrooms.value = UiState.Success(it)
                }
            } catch (e: Exception) {
                _chatrooms.value = if (e.message.toString().contains("HTTP 404")) UiState.Success(
                    listOf()
                ) else UiState.Error("error: ${e.message}")
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }

    fun getDetailChatroom(roomId: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getChatroomDetail(roomId).catch { error ->
                    _detailChatroom.value = UiState.Error("error: ${error.message}")
                    eventChannel.send(MyEvent.MessageEvent("error: ${error.message}"))
                }.collect {
                    _detailChatroom.value = UiState.Success(it)
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
                eventChannel.send(MyEvent.MessageEvent("error: ${e.message}"))
            }
        }
    }
}