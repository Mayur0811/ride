package com.bayride.data.models.dto

data class CompleteHistoryModel(
    var date: String,
    var driverName: String,
    var rating: Float,
    var driverImage:String,
    var fromLocation:String,
    var toLocation:String,
    var price:Int
)
