package com.bangkit.ecoeasemitra.data.room.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "address")
data class Address(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "district")
    val district: String,

    @ColumnInfo(name = "detail")
    val detail: String,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "selected")
    val selected: Boolean = false,

) : Parcelable
