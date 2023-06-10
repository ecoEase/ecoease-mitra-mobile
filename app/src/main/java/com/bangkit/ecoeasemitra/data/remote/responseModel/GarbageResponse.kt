package com.bangkit.ecoeasemitra.data.remote.responseModel

import com.bangkit.ecoeasemitra.data.room.model.Garbage
import com.google.gson.annotations.SerializedName

data class GarbageResponse(

	@field:SerializedName("data")
	val data: List<GarbageItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class GarbageItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("price")
	val price: Long? = null,

	@field:SerializedName("url_photo")
	val urlPhoto: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

fun GarbageItem.toGarbage(): Garbage = Garbage(
	id = this.id.orEmpty(),
	urlPhoto = this.urlPhoto.orEmpty(),
	type = this.type.orEmpty(),
	price = this.price ?: 0
)
