package com.bangkit.ecoeasemitra.data.remote.responseModel.fcm

data class FCMResponse(
	val canonicalIds: Int,
	val success: Int,
	val failure: Int,
	val results: List<ResultsItem>,
	val multicastId: Long
)

data class ResultsItem(
	val messageId: String
)