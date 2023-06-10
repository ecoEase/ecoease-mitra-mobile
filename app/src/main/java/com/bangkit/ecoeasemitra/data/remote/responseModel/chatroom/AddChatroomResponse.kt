package com.bangkit.ecoeasemitra.data.remote.responseModel.chatroom

import com.google.gson.annotations.SerializedName

data class AddChatroomResponse(

	@field:SerializedName("data")
	val data: Data?,

	@field:SerializedName("message")
	val message: String
)

data class Data(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("mitra_id")
	val mitraId: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
