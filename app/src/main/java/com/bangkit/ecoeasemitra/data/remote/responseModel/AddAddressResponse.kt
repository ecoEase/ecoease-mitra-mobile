package com.bangkit.ecoeasemitra.data.remote.responseModel

import com.google.gson.annotations.SerializedName

data class AddAddressResponse(

	@field:SerializedName("address")
	val address: Address,

	@field:SerializedName("message")
	val message: String
)

data class Address(

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
