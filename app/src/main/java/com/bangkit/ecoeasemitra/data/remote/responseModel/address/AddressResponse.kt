package com.bangkit.ecoeasemitra.data.remote.responseModel.address

import com.google.gson.annotations.SerializedName

data class AddressResponse(
	@field:SerializedName("data")
	val data: List<AddressItem>?,

	@field:SerializedName("message")
	val message: String
)
