package com.bangkit.ecoeasemitra.data.room.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.*
import kotlinx.parcelize.Parcelize

enum class StatusOrderItem{
    NOT_TAKEN, TAKEN, ON_PROCESS, FINISHED, CANCELED
}

@Parcelize
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "status")
    val status: StatusOrderItem,

    @ColumnInfo(name = "total_transaction")
    val totalTransaction: Long,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "mitra_id")
    val mitraId: String?,

    @ColumnInfo(name = "location_id")
    val locationId: String?,

    @ColumnInfo(name = "address_id")
    val addressId: String,

    @ColumnInfo(name = "created")
    val created: String,
) : Parcelable

data class GarbageTransactionWithDetail(
    @Embedded
    val orderInfo: DetailTransaction,

    @Relation(parentColumn = "garbageId", entity = Garbage::class, entityColumn = "id")
    val garbage: Garbage
)
data class OrderWithDetailTransaction(
    @Embedded
    val order: Order,

    @Relation(parentColumn = "id", entity = DetailTransaction::class, entityColumn = "orderId")
    val items: List<GarbageTransactionWithDetail>,

    @Relation(parentColumn = "user_id", entity = User::class, entityColumn = "id")
    val user: User,

    @Relation(parentColumn = "address_id", entity = Address::class, entityColumn = "id")
    val address: Address,

    @Relation(parentColumn = "location_id", entity = Location::class, entityColumn = "id")
    val location: Location?,

    @Relation(parentColumn = "mitra_id", entity = Mitra::class, entityColumn = "id")
    val mitra: Mitra?,
)