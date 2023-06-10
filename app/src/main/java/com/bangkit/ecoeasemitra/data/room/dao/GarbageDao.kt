package com.bangkit.ecoeasemitra.data.room.dao

import androidx.room.*
import com.bangkit.ecoeasemitra.data.room.model.Garbage

@Dao
interface GarbageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGarbage(garbage: Garbage)

    @Delete
    suspend fun deleteGarbage(garbage: Garbage)

    @Query("SELECT * FROM garbage")
    fun getAllGarbage() : List<Garbage>

    @Query("DELETE FROM garbage")
    suspend fun deleteAllGarbage()
}