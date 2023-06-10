package com.bangkit.ecoeasemitra.data.model.request

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class Register(
    val photoFile: MultipartBody.Part,
    val first_name: RequestBody,
    val last_name: RequestBody,
    val email: RequestBody,
    val password: RequestBody,
    val phone_number: RequestBody,
)
