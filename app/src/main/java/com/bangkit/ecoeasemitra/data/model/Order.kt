package com.bangkit.ecoeasemitra.data.model

data class Order(
    val garbageList: List<GarbageAdded?>,
    val total: Long
)
data class GarbageAdded(
    val garbage: com.bangkit.ecoeasemitra.data.room.model.Garbage,
    val amount: Int,
    val totalPrice: Long
)