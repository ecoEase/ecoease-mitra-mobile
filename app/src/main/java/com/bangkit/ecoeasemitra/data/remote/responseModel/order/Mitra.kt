package com.bangkit.ecoeasemitra.data.remote.responseModel.order

import com.google.gson.annotations.SerializedName

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

    @field:SerializedName("phone_number")
    val phoneNumber: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String,

    @field:SerializedName("fcm_token")
    val fcmToken: String,
)
