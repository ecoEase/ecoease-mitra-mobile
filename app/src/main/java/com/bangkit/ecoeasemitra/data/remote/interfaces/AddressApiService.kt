package com.bangkit.ecoeasemitra.data.remote.interfaces

import com.bangkit.ecoeasemitra.data.model.request.Address
import com.bangkit.ecoeasemitra.data.remote.responseModel.AddAddressResponse
import com.bangkit.ecoeasemitra.data.remote.responseModel.address.AddressResponse
import com.bangkit.ecoeasemitra.data.remote.responseModel.DeleteAddressResponse
import com.bangkit.ecoeasemitra.data.remote.responseModel.address.SelectedAddressResponse
import retrofit2.http.*

interface AddressApiService {
    @GET("address")
    suspend fun getAll(@Header("Authorization") token: String, @Query("userId") userId: String): AddressResponse

    @POST("address")
    suspend fun addNewAddress(@Header("Authorization") token: String, @Body address: Address): AddAddressResponse

    @PUT("address/use/{id}")
    suspend fun selectUseAddress(@Header("Authorization") token: String, @Path("id") id: String): AddressResponse
    @DELETE("address/delete/{id}")
    suspend fun deleteAddress(@Header("Authorization") token: String, @Path("id") id: String): DeleteAddressResponse

    @GET("address/selected/{id}")
    suspend fun getSelectedFromUser(@Header("Authorization") token: String, @Path("id") id: String): SelectedAddressResponse
}