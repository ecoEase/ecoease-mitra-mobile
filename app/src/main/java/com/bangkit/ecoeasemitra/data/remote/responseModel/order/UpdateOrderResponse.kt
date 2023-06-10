package com.bangkit.ecoeasemitra.data.remote.responseModel.order

import com.google.gson.annotations.SerializedName

data class UpdateOrderResponse(
	@field:SerializedName("data")
	val data: List<Int>? = null,

	@field:SerializedName("message")
	val message: String
)
