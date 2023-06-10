package com.bangkit.ecoeasemitra.data.remote.responseModel.order

import com.google.gson.annotations.SerializedName

data class DetailTransaction(

    @field:SerializedName("garbage_id")
    val garbageId: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("total")
    val total: Int,

    @field:SerializedName("qty")
    val qty: Int,

    @field:SerializedName("order_id")
    val orderId: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String
)