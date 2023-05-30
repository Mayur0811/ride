package com.bayride.data.models.dto

data class OnGoingHistoryModel(
    var driverImage: Int,
    var driverName: String,
    var driverPhoneNo: String,
    var price: Int,
    var bookingId: String,
    var date: String,
    var fromLocation: String,
    var toLocation: String
)
