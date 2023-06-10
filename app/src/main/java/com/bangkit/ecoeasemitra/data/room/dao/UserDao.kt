package com.bangkit.ecoeasemitra.data.room.dao

import androidx.room.*
import com.bangkit.ecoeasemitra.data.room.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM user LIMIT 1")
    fun getUser() : User
    @Query("DELETE FROM user")
    fun deleteAll()
}