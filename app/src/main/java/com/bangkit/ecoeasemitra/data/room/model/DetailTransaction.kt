package com.bangkit.ecoeasemitra.data.room.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import kotlinx.parcelize.Parcelize

//this is junction table between order and garbage tables
@Parcelize
@Entity(
    primaryKeys = ["orderId", "garbageId"],
    foreignKeys = [
        ForeignKey(entity = Order::class, parentColumns = ["id"], childColumns = ["orderId"]),
        ForeignKey(entity = Garbage::class, parentColumns = ["id"], childColumns = ["garbageId"])
    ]
)
data class DetailTransaction(
    val orderId: String,
    @ColumnInfo(index = true)
    val garbageId: String,
    val qty: Int,
    val total: Long,
) : Parcelable
