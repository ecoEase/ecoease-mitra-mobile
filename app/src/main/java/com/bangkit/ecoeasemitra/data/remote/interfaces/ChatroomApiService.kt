package com.bangkit.ecoeasemitra.data.remote.interfaces

import com.bangkit.ecoeasemitra.data.model.request.Chatroom
import com.bangkit.ecoeasemitra.data.remote.responseModel.chatroom.AddChatroomResponse
import com.bangkit.ecoeasemitra.data.remote.responseModel.chatroom.ChatroomResponse
import com.bangkit.ecoeasemitra.data.remote.responseModel.chatroom.DeleteChatroomResponse
import retrofit2.http.*

interface ChatroomApiService{
    @GET("chatrooms")
    suspend fun getChatrooms(@Header("Authorization") token: String, @Query("userId") userId: String? = null, @Query("mitraId") mitraId: String? = null): ChatroomResponse

    @POST("chatrooms")
    suspend fun addChatroom(@Header("Authorization") token: String, @Body body: Chatroom): AddChatroomResponse

    @DELETE("chatrooms/delete/{id}")
    suspend fun deleteChatroom(@Header("Authorization") token: String, @Path("id") id: String): DeleteChatroomResponse

}