package com.bangkit.ecoeasemitra.data.remote.responseModel.chatroom

import com.google.gson.annotations.SerializedName

data class DetailChatroomResponse(
    @field:SerializedName("data")
    val data: ChatRoomItem?,

    @field:SerializedName("message")
    val message: String
)