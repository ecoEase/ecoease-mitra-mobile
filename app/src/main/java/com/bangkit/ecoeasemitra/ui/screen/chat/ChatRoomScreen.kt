package com.bangkit.ecoeasemitra.ui.screen.chat

import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.data.firebase.FireBaseRealtimeDatabase
import com.bangkit.ecoeasemitra.data.firebase.FireBaseRealtimeDatabase.getCurrentChats
import com.bangkit.ecoeasemitra.data.model.Message
import com.bangkit.ecoeasemitra.data.model.request.FCMNotification
import com.bangkit.ecoeasemitra.data.model.request.Notification
import com.bangkit.ecoeasemitra.data.remote.responseModel.chatroom.ChatRoomItem
import com.bangkit.ecoeasemitra.data.room.model.User
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.bangkit.ecoeasemitra.ui.component.ChatBubble
import com.bangkit.ecoeasemitra.ui.component.ErrorHandler
import com.bangkit.ecoeasemitra.ui.component.TextInput
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.StateFlow
import java.util.*

private val gsonPretty = GsonBuilder().setPrettyPrinting().create()

@Composable
fun ChatRoomScreen(
    roomId: String,
    getChatroomDetail: () -> Unit,
    chatroomDetailUiState: StateFlow<UiState<ChatRoomItem>>,
    getCurrentUser: () -> Unit,
    reloadGetCurrentUser: () -> Unit,
    userUiState: StateFlow<UiState<User>>,
    sendNotification: (FCMNotification) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messagesRef = FireBaseRealtimeDatabase.createMessageRef(roomId)
    var messageTxt: String by rememberSaveable { mutableStateOf("") }
    var chats: MutableList<Message> = remember { mutableStateListOf() }
    var loading by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    var username by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var token: String? by remember { mutableStateOf(null) }
    var otherUsers = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        messagesRef.getCurrentChats().addOnCompleteListener {
            if (it.isSuccessful) {
                chats.clear()
                chats.addAll(it.result)
            }
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener { token = it.result }
    }

    LaunchedEffect(chats.size) {
        if (chats.size != 0) {
            lazyListState.animateScrollToItem(chats.size)
            //maping all chatroom user fcm token
            val filtered = chats.filter { it.token != token }.toSet()
            val setOfFilteredToken = filtered.map { it.token ?: "" }.toSet().toMutableStateList()
            otherUsers = setOfFilteredToken

        }
    }

    DisposableEffect(Unit) {
        val childEventListener = FireBaseRealtimeDatabase.chatChildEventListener { chats.add(it) }
        messagesRef.addChildEventListener(childEventListener)
        onDispose {
            messagesRef.removeEventListener(childEventListener)
        }
    }

    fun handleSendMessage(messageBody: Message, receiverFCMToken: String) {
        try {
            messagesRef
                .push()
                .setValue(
                    messageBody
                ) { error, _ -> if (error != null) throw Exception(error.message) }

            Log.d("TAG", "handleSendMessage: ${gsonPretty.toJson(otherUsers.toList())} ")
            val notificationBody =
                Notification(body = messageTxt, title = fullname, subTitle = messageTxt)
            sendNotification(
                FCMNotification(
                    to = receiverFCMToken,
                    notification = notificationBody
                )
            )
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "error: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            messageTxt = ""
        }
    }

    fun setUsername(firstname: String, lastname: String, email: String) {
        fullname = "$firstname $lastname"
        username = email
    }

    val animatedIconBgColor by animateColorAsState(
        targetValue = if (messageTxt.isNotEmpty()) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
        animationSpec = tween(200)
    )

    chatroomDetailUiState.collectAsState().value.let { uiState ->
        when (uiState) {
            is UiState.Loading -> {
                Loader(modifier = Modifier.fillMaxWidth())
                getChatroomDetail()
            }
            is UiState.Success -> {
                //change if for user app, using user, if for mitra app using mitra
                setUsername(
                    firstname = uiState.data.mitra.firstName,
                    lastname = uiState.data.mitra.lastName,
                    email = uiState.data.mitra.email
                )

                Column(modifier = modifier.fillMaxSize()) {
                    Column(modifier = Modifier.weight(1f)) {
                        AnimatedVisibility(
                            visible = !loading,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 32.dp),
                                contentPadding = PaddingValues(bottom = 48.dp),
                                state = lazyListState
                            ) {
                                items(chats.toList()) { message ->
                                    ChatBubble(
                                        message = message.text ?: "",
                                        sender = message.name ?: "",
                                        isOwner = message.username == username,
                                        date = DateUtils.getRelativeTimeSpanString(
                                            message.timeStamp ?: 0
                                        )
                                            .toString()
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        TextInput(
                            placeHolder = "Type message",
                            value = messageTxt,
                            onValueChange = { it -> messageTxt = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 52.dp)
                        )
                        IconButton(
                            onClick = {
                                handleSendMessage(
                                    messageBody = Message(
                                        token = token,
                                        text = messageTxt,
                                        name = fullname,
                                        username = username,
                                        timeStamp = Date().time
                                    ),
                                    receiverFCMToken = uiState.data.user.fcmToken//because this is mitra app, token receiver must be from user
                                )
                            }, modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(animatedIconBgColor)
                                .padding(8.dp)
                                .align(Alignment.CenterEnd),
                            enabled = messageTxt.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.Send,
                                tint = Color.White,
                                contentDescription = "send message icon",
                            )
                        }
                    }
                }
            }
            is UiState.Error -> {
                ErrorHandler(errorText = uiState.errorMessage, onReload = {
                    reloadGetCurrentUser()
                })
            }
        }
    }
}

@Composable
private fun Loader(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}