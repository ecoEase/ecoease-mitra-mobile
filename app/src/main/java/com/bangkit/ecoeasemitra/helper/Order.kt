package com.bangkit.ecoeasemitra.helper

import com.bangkit.ecoeasemitra.data.remote.responseModel.order.OrderDetailData
import com.bangkit.ecoeasemitra.data.room.model.*

fun OrderDetailData.toOrderWithDetailTransaction(): OrderWithDetailTransaction {
    val address = Address(
        id = this.address.id,
        name = this.address.name,
        city = this.address.city,
        district = this.address.district,
        detail = this.address.detail,
        selected = this.address.selected,
    )

    val listItems = this.garbages.map { garbage ->
        GarbageTransactionWithDetail(
            garbage = Garbage(
                id = garbage.id,
                price = garbage.price.toLong(),
                type = garbage.type,
                urlPhoto = garbage.urlPhoto,
            ),
            orderInfo = DetailTransaction(
                garbageId = garbage.detailtransaction.garbageId,
                orderId = garbage.detailtransaction.orderId,
                qty = garbage.detailtransaction.qty,
                total = garbage.detailtransaction.total.toLong(),
            )
        )
    }

    val location = if(this.location != null) Location(
        id = this.location.id,
        latitude = this.location.latitude,
        longitude = this.location.longitude
    ) else null

    val order = Order(
        id = this.id,
        status = StatusOrderItem.valueOf(this.status),
        userId = this.userId,
        mitraId = this.mitraId,
        addressId = this.addressId,
        locationId = this.locationId,
        totalTransaction = this.totalTransaction.toLong(),
        created = this.createdAt
    )

    val user = User(
        id = this.user.id,
        email = this.user.email,
        firstName = this.user.firstName,
        lastName = this.user.lastName,
        password = this.user.password,
        phoneNumber = this.user.phoneNumber,
        urlPhotoProfile = this.user.urlPhotoProfile,
        fcmToken = this.user.fcmToken,
    )

    val mitra = if(this.mitra != null) Mitra(
        id = this.mitra.id,
        firstName = this.mitra.firstName,
        lastName = this.mitra.firstName,
        email = this.mitra.firstName,
        password = this.mitra.password,
        urlPhotoProfile = this.mitra.urlPhotoProfile,
        fcmToken = this.mitra.fcmToken,
    ) else null

    return OrderWithDetailTransaction(
        order = order,
        address = address,
        items = listItems,
        location = location,
        user = user,
        mitra = mitra
    )
}