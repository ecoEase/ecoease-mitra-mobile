package com.bangkit.ecoeasemitra.data.room.dao

import androidx.room.*
import com.bangkit.ecoeasemitra.data.room.model.Mitra

@Dao
interface MitraDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMitra(mitra: Mitra)

    @Delete
    suspend fun deleteUser(mitra: Mitra)

    @Query("SELECT * FROM mitra LIMIT 1")
    fun getMitra() : Mitra

    @Query("DELETE FROM mitra")
    fun deleteAll()
}