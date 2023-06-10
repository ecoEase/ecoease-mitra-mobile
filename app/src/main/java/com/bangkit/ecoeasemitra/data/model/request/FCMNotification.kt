package com.bangkit.ecoeasemitra.data.model.request

data class FCMNotification(
    val to: String,
    val notification: Notification
)

data class Notification(
    val body: String,
    val title: String,
    val subTitle: String
)
