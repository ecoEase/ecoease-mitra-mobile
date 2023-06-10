package com.bangkit.ecoeasemitra.data.model

import com.bangkit.ecoeasemitra.data.room.model.StatusOrderItem

data class OrderHistory(
    val id: String,
    val items: List<String>,
    val date: String,
    val price: Int,
    val status: StatusOrderItem
)
