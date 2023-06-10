package com.bangkit.ecoeasemitra.data.remote.responseModel.order

import com.google.gson.annotations.SerializedName

data class Location(

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("latitude")
    val latitude: Double,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("longitude")
    val longitude: Double,

    @field:SerializedName("updatedAt")
    val updatedAt: String
)

fun Location.toLocation(): com.bangkit.ecoeasemitra.data.room.model.Location = com.bangkit.ecoeasemitra.data.room.model.Location(
    id = this.id,
    latitude = this.latitude.toDouble(),
    longitude = this.longitude.toDouble()
)