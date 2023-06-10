package com.bangkit.ecoeasemitra.ui.screen.chat

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.data.event.MyEvent
import com.bangkit.ecoeasemitra.data.firebase.FireBaseRealtimeDatabase
import com.bangkit.ecoeasemitra.data.firebase.FireBaseRealtimeDatabase.getAllRoomsKey
import com.bangkit.ecoeasemitra.data.model.Chatroom
import com.bangkit.ecoeasemitra.helper.generateUUID
import com.bangkit.ecoeasemitra.ui.component.Avatar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UsersChatsScreen(
    navHostController: NavHostController,
    onCreateNewChatroom: () -> Unit,
    onLoadChatRooms: () -> Unit,
    onDeleteRoom: (id: String, key: String) -> Unit,
    eventFlow: Flow<MyEvent>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(false) }
    var rooms: MutableList<Chatroom> = remember { mutableStateListOf() }
    val chatroomRef = FireBaseRealtimeDatabase.createRoomRef()
    LaunchedEffect(Unit) {
        loading = true
        chatroomRef.getAllRoomsKey().addOnCompleteListener {
            loading = false
            if (it.isSuccessful) {
                rooms.clear()
                rooms.addAll(it.result.toMutableList())
            }
        }
        eventFlow.collect { event ->
            when (event) {
                is MyEvent.MessageEvent -> Toast.makeText(
                    context,
                    event.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    DisposableEffect(Unit) {
        val childEventListener =
            FireBaseRealtimeDatabase.roomChildEventListener(
                onChildAdded = { newRoom ->
                    rooms.add(newRoom)
                },
                onChildRemoved = { deletedRom ->
                    rooms.remove(deletedRom)
                },
            )
        chatroomRef.addChildEventListener(childEventListener)
        onDispose {
            chatroomRef.removeEventListener(childEventListener)
        }
    }

    val refreshState = rememberCoroutineScope()
    var refreshing: Boolean by remember { mutableStateOf(false) }

    fun refresh() = refreshState.launch {
        refreshing = true
        delay(200)
        onLoadChatRooms()
        refreshing = false
    }

    val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            if (loading) CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            AnimatedVisibility(
                visible = !loading, modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(rooms.toList(), key = { it.key ?: generateUUID() }) { room ->
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        room?.let {
                                            Screen.ChatRoom.setTitle(it.key)
                                            val roomChatRoute = Screen.ChatRoom.createRoute(it.value ?: "")
                                            navHostController.navigate(roomChatRoute)
                                        }
                                    },
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            )
                            {
                                Avatar(imageUrl = "https://images.unsplash.com/photo-1596815064285-45ed8a9c0463?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=366&q=80")
                                Text(text = room.value ?: "")

                            }
                            IconButton(onClick = {
                                onDeleteRoom(room.key, room.value ?: "")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "delete"
                                )
                            }
                        }
                    }
                }
            }
        }
        PullRefreshIndicator(refreshing = refreshing, state = pullRefreshState)
        FloatingActionButton(onClick = { onCreateNewChatroom() }) {
            Icon(imageVector = Icons.Default.Chat, contentDescription = "new chatroom")
        }
    }
}