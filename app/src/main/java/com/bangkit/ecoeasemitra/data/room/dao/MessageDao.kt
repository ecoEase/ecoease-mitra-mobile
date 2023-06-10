package com.bangkit.ecoeasemitra.data.room.dao

import androidx.room.*
import com.bangkit.ecoeasemitra.data.room.model.Message

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMessage(message: Message)

    @Delete
    suspend fun deleteMessage(message: Message)

    @Query("SELECT * FROM messages")
    fun getMessages() : List<Message>

    @Query("SELECT * FROM messages WHERE id = :id")
    fun getSelectedMessages(id: String) : Message
}