package com.bangkit.ecoeasemitra.data.remote.responseModel.address

import com.google.gson.annotations.SerializedName

data class SelectedAddressResponse(
    @field:SerializedName("data")
    val data: AddressItem?,

    @field:SerializedName("message")
    val message: String
)