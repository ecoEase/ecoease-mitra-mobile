package com.bangkit.ecoeasemitra.data.model.request

data class OrderWithDetail(
	val location: Location?,
	val order: Order,
	val detailTransactions: List<DetailTransactionsItem>,
)

data class Order(
	val total_transaction: Int,
	val user_id: String,
	val address_id: String,
	val status: String
)

data class Location(
	val latitude: Double,
	val longitude: Double
)

data class DetailTransactionsItem(
	val garbage_id: String,
	val total: Int,
	val qty: Int
)

