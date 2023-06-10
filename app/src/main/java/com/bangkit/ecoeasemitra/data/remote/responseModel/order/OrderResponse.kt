package com.bangkit.ecoeasemitra.data.remote.responseModel.order

import com.google.gson.annotations.SerializedName

data class OrderResponse(

	@field:SerializedName("data")
	val data: List<OrderDetailData>? = null,

	@field:SerializedName("message")
	val message: String
)