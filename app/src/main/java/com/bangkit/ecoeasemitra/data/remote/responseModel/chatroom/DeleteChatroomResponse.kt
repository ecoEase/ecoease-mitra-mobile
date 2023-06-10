package com.bangkit.ecoeasemitra.data.remote.responseModel.chatroom

import com.google.gson.annotations.SerializedName

data class DeleteChatroomResponse(

	@field:SerializedName("data")
	val data: Int?,

	@field:SerializedName("message")
	val message: String
)
