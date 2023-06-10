package com.bangkit.ecoeasemitra.data.event

sealed class MyEvent {
    data class MessageEvent(val message: String): MyEvent()
}