package com.bangkit.ecoeasemitra.data.room.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "garbage")
data class Garbage(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "url_photo")
    val urlPhoto: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "price")
    val price: Long
) : Parcelable
