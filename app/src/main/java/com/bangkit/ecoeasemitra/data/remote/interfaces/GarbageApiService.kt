package com.bangkit.ecoeasemitra.data.remote.interfaces

import com.bangkit.ecoeasemitra.data.remote.responseModel.GarbageResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface GarbageApiService{
    @GET("garbage")
    suspend fun get(@Header("Authorization") token: String): GarbageResponse
}