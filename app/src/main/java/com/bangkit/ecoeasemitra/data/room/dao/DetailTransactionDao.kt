package com.bangkit.ecoeasemitra.data.room.dao

import androidx.room.*
import com.bangkit.ecoeasemitra.data.room.model.DetailTransaction

@Dao
interface DetailTransactionDao {
    @Insert
    suspend fun addDetailTransaction(orderGarbage: DetailTransaction)

    @Delete
    suspend fun deleteDetailTransaction(orderGarbage: DetailTransaction)

    @Query("DELETE FROM DetailTransaction")
    fun deleteAllDetailTransaction()
}