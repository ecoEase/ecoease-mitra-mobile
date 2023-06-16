package com.bangkit.ecoeasemitra.config

import com.bangkit.ecoeasemitra.data.remote.interfaces.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig{
    companion object{
        private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        private val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        private val retrofit = Retrofit.Builder().apply {
            baseUrl("https://20230615t144626-dot-ecoease-389914.appspot.com/api/v1/")
            addConverterFactory(GsonConverterFactory.create())
            client(client)
        }.build()

        private val fcmRetrofit = Retrofit.Builder().apply {
            baseUrl("https://fcm.googleapis.com/")
            addConverterFactory(GsonConverterFactory.create())
            client(client)
        }.build()

        fun getGarbageApiService(): GarbageApiService = retrofit.create(GarbageApiService::class.java)
        fun getMitraApiService(): MitraApiService = retrofit.create(MitraApiService::class.java)
        fun getAddressApiService(): AddressApiService = retrofit.create(AddressApiService::class.java)
        fun getOrderApiService(): OrderApiService = retrofit.create(OrderApiService::class.java)
        fun getChatroomApiService(): ChatroomApiService = retrofit.create(ChatroomApiService::class.java)
        fun getFCMServerApiService(): FCMServerApiService = retrofit.create(FCMServerApiService::class.java)
        fun getFCMClientApiService(): FCMClientApiService = fcmRetrofit.create(FCMClientApiService::class.java)
    }
}