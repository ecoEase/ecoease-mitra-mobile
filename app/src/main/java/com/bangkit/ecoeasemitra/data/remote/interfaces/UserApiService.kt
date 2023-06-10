package com.bangkit.ecoeasemitra.data.remote.interfaces

import com.bangkit.ecoeasemitra.data.model.request.Login
import com.bangkit.ecoeasemitra.data.remote.responseModel.RegisterResponse
import com.bangkit.ecoeasemitra.data.remote.responseModel.UserLoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApiService{
    @POST("login")
    suspend fun login(@Body login: Login): UserLoginResponse

    @Multipart
    @POST("register")
    suspend fun register(
        @Part("firstName") firstName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("phone_number") phone_number: RequestBody,
        @Part photoFile: MultipartBody.Part,
    ) : RegisterResponse
}