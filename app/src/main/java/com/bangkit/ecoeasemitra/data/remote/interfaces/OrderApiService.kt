package com.bangkit.ecoeasemitra.data.remote.interfaces

import com.bangkit.ecoeasemitra.data.model.request.OrderWithDetail
import com.bangkit.ecoeasemitra.data.model.request.UpdateOrder
import com.bangkit.ecoeasemitra.data.remote.responseModel.order.AddOrderDetailResponse
import com.bangkit.ecoeasemitra.data.remote.responseModel.order.OrderDetailResponse
import com.bangkit.ecoeasemitra.data.remote.responseModel.order.OrderResponse
import com.bangkit.ecoeasemitra.data.remote.responseModel.order.UpdateOrderResponse
import retrofit2.http.*

interface OrderApiService{
    @GET("orders")
    suspend fun getAll(@Header("Authorization") token: String): OrderResponse

    @GET("orders")
    suspend fun getByUser(@Header("Authorization") token: String, @Query("userId") userId: String): OrderResponse

    @GET("orders")
    suspend fun getByMitra(@Header("Authorization") token: String, @Query("mitraId") mitraId: String): OrderResponse

    @GET("orders")
    suspend fun getById(@Header("Authorization") token: String, @Query("id") id: String): OrderDetailResponse

    @GET("orders/available")
    suspend fun getAvailable(@Header("Authorization") token: String): OrderResponse

    @POST("orders/with-detail-transactions")
    suspend fun addNewOrder(@Header("Authorization") token: String, @Body body: OrderWithDetail): AddOrderDetailResponse
    @PUT("orders/cancel")
    suspend fun cancelOrder(@Header("Authorization") token: String, @Body body: UpdateOrder): UpdateOrderResponse
    //mitra only
    @PUT("orders/pickup")
    suspend fun pickupOrder(@Header("Authorization") token: String, @Body body: UpdateOrder): UpdateOrderResponse
    @PUT("orders/update-status")
    suspend fun updateOrderStatus(@Header("Authorization") token: String, @Body body: UpdateOrder): UpdateOrderResponse
}
