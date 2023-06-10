package com.bangkit.ecoeasemitra.data.remote.responseModel.order

import com.google.gson.annotations.SerializedName


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
    val updatedAt: String,

    @field:SerializedName("fcm_token")
    val fcmToken: String,
)