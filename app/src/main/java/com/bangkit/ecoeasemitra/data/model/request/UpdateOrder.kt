package com.bangkit.ecoeasemitra.data.model.request

import com.bangkit.ecoeasemitra.data.room.model.StatusOrderItem

data class UpdateOrder(
    val id: String,
    val status: StatusOrderItem,
    val mitraId: String? = null,
)
