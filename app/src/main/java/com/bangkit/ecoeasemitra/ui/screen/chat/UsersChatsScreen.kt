package com.bangkit.ecoeasemitra.ui.screen.chat

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.data.event.MyEvent
import com.bangkit.ecoeasemitra.data.firebase.FireBaseRealtimeDatabase
import com.bangkit.ecoeasemitra.data.firebase.FireBaseRealtimeDatabase.getAllRoomsKey
import com.bangkit.ecoeasemitra.data.model.Chatroom
import com.bangkit.ecoeasemitra.data.remote.responseModel.chatroom.ChatRoomItem
import com.bangkit.ecoeasemitra.helper.formatDate
import com.bangkit.ecoeasemitra.helper.generateUUID
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.bangkit.ecoeasemitra.ui.component.Avatar
import com.bangkit.ecoeasemitra.ui.component.DialogBox
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey
import com.google.gson.GsonBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


private val gsonPretty = GsonBuilder().setPrettyPrinting().create()
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UsersChatsScreen(
    navHostController: NavHostController,
    onLoadChatRooms: () -> Unit,
    chatroomsUiState: StateFlow<UiState<List<ChatRoomItem>>>,
    onDeleteRoom: (key: String, id: String) -> Unit,
    eventFlow: Flow<MyEvent>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(false) }
    var rooms: MutableList<Chatroom> = remember { mutableStateListOf() }
    var userRoomsId: MutableList<String> = remember { mutableStateListOf() }
    val chatroomRef = FireBaseRealtimeDatabase.createRoomRef()
    val refreshState = rememberCoroutineScope()
    var refreshing: Boolean by remember { mutableStateOf(false) }
    var openDeleteDialog by remember { mutableStateOf(false) }

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

    fun refresh() = refreshState.launch {
        refreshing = true
        delay(200)
        onLoadChatRooms()
        refreshing = false
    }

    val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)

    fun handleFilterFirebaseChatroom(userRooms: List<ChatRoomItem>){
        userRoomsId = userRooms.map { it.id }.toMutableList()
        rooms = rooms.filter { userRoomsId.contains(it.value) }.toMutableList()
        Log.d("TAG", "handleFilterFirebaseChatroom: ${gsonPretty.toJson(userRoomsId)}")
        Log.d("TAG", "filtered room: ${gsonPretty.toJson(rooms)}")
    }

    fun handleDeleteChatroom(index: Int){
        onDeleteRoom(rooms[index].key ?: "", rooms[index].value ?: "")
    }

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
            chatroomsUiState.collectAsState(initial = UiState.Loading).value.let {uiState ->
                when(uiState){
                    is UiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        onLoadChatRooms()
                    }
                    is UiState.Success -> {
                        Log.d("TAG", "UsersChatsScreen: ${gsonPretty.toJson(uiState.data)}")
                        handleFilterFirebaseChatroom(uiState.data)
                        AnimatedVisibility(
                            visible = !loading, modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                itemsIndexed(uiState.data.toList()) { index, room ->
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    room?.let {
                                                        Screen.ChatRoom.setTitle("${room.user.firstName} ${room.user.lastName}")
                                                        val roomChatRoute =
                                                            Screen.ChatRoom.createRoute(it.id ?: "")
                                                        navHostController.navigate(roomChatRoute)
                                                    }
                                                },
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        )
                                        {
                                            Avatar(imageUrl = room.user.urlPhotoProfile)
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = "${room.user.firstName} ${room.user.lastName}")
                                                Text(text = "created: ${formatDate(room.createdAt)}", style = MaterialTheme.typography.caption.copy(
                                                    color = DarkGrey
                                                ), modifier = Modifier.align(Alignment.End))
                                            }
                                            IconButton(onClick = { openDeleteDialog = true }) {
                                                Icon(imageVector = Icons.Default.Delete, contentDescription = "delete chatroom")
                                            }
                                        }
                                    }
                                    DialogBox(text = stringResource(R.string.delete_chatroom_warning), onAccept = { handleDeleteChatroom(index) }, isOpen = openDeleteDialog, onDissmiss = { openDeleteDialog = false })
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        Text(uiState.errorMessage)
                    }
                }
            }
        }
        PullRefreshIndicator(refreshing = refreshing, state = pullRefreshState)
    }
}