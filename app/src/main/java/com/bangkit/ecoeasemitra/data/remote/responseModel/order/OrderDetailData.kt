package com.bangkit.ecoeasemitra.data.remote.responseModel.order

import com.bangkit.ecoeasemitra.data.remote.responseModel.Address
import com.google.gson.annotations.SerializedName

data class OrderDetailData(

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("total_transaction")
    val totalTransaction: Int,

    @field:SerializedName("address")
    val address: Address,

    @field:SerializedName("user")
    val user: User,

    @field:SerializedName("address_id")
    val addressId: String,

    @field:SerializedName("garbages")
    val garbages: List<GarbagesItem>,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("user_id")
    val userId: String,

    @field:SerializedName("mitra_id")
    val mitraId: String?,

    @field:SerializedName("mitra")
    val mitra: Mitra?,

    @field:SerializedName("location_id")
    val locationId: String?,

    @field:SerializedName("location")
    val location: Location?,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String
)
