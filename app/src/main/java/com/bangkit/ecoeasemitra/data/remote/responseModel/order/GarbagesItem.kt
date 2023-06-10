package com.bangkit.ecoeasemitra.data.remote.responseModel.order

import com.google.gson.annotations.SerializedName


data class GarbagesItem(

    @field:SerializedName("detailtransaction")
    val detailtransaction: DetailTransaction,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("price")
    val price: Int,

    @field:SerializedName("url_photo")
    val urlPhoto: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("type")
    val type: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String
)
