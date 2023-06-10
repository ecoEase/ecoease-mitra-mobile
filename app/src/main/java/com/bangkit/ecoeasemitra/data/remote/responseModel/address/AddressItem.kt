package com.bangkit.ecoeasemitra.data.remote.responseModel.address

import com.bangkit.ecoeasemitra.data.room.model.Address
import com.google.gson.annotations.SerializedName


data class AddressItem(

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("city")
    val city: String,

    @field:SerializedName("user_id")
    val userId: String,

    @field:SerializedName("district")
    val district: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("detail")
    val detail: String,

    @field:SerializedName("selected")
    val selected: Boolean,

    @field:SerializedName("deleted")
    val deleted: Boolean,

    @field:SerializedName("updatedAt")
    val updatedAt: String
)

fun AddressItem.toAddress(): Address = Address(
    id = this.id,
    name = this.name,
    city = this.city,
    district = this.district,
    detail = this.detail,
    selected = this.selected,
)