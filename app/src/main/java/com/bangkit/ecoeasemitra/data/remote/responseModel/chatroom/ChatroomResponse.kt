package com.bangkit.ecoeasemitra.data.remote.responseModel.chatroom

import com.google.gson.annotations.SerializedName

data class ChatroomResponse(

	@field:SerializedName("data")
	val data: List<ChatRoomItem>?,

	@field:SerializedName("message")
	val message: String
)

data class User(

	@field:SerializedName("firstName")
	val firstName: String,

	@field:SerializedName("lastName")
	val lastName: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("password")
	val password: String,

	@field:SerializedName("url_photo_profile")
	val urlPhotoProfile: String,

	@field:SerializedName("phone_number")
	val phoneNumber: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class ChatRoomItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("User")
	val user: User,

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("mitra")
	val mitra: Mitra,

	@field:SerializedName("mitra_id")
	val mitraId: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class Mitra(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("password")
	val password: String,

	@field:SerializedName("url_photo_profile")
	val urlPhotoProfile: String,

	@field:SerializedName("last_name")
	val lastName: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("first_name")
	val firstName: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
