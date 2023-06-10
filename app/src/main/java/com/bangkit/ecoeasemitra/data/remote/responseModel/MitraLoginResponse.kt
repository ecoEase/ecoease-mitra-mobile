package com.bangkit.ecoeasemitra.data.remote.responseModel

import com.bangkit.ecoeasemitra.data.room.model.Mitra
import com.bangkit.ecoeasemitra.data.room.model.User
import com.google.gson.annotations.SerializedName

data class MitraLoginResponse(

	@field:SerializedName("data")
	val data: MitraData?,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("token")
	val token: String
)

data class MitraData(

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

	@field:SerializedName("fcm_token")
	val fcmToken: String?,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

fun MitraData.toMitra(): Mitra = Mitra(
	id = this.id,
	firstName = this.firstName,
	lastName = this.lastName,
	email = this.email,
	phoneNumber = this.phoneNumber,
	password = this.password,
	urlPhotoProfile = this.urlPhotoProfile,
	fcmToken = this.fcmToken,
)
