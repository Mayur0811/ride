package com.bayride.data.models.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class DriverRequestListModel(
    val Message: String? = null,
    val Status: Int? =null,
    val info: List<DriverRequestInfo>? =null
)

@Parcelize
@Serializable
data class DriverRequestInfo(
    val created_at: String?,
    val driver_bid_by: Int?,
    val driver_bid_price: String?,
    val driver_bid_status: Int?,
    val fare_driver_bid_id: Int?,
    val fare_id: Int?,
    val real_star: Double?,
    val total_review: Int?,
    val user_first_name: String?,
    val user_last_name: String?,
    val user_name: String?,
    val user_phone_number: String?,
    val user_profile_pic: String?,
    val vehicle_added_by: Int?,
    val vehicle_brand: String?,
    val vehicle_colour: String?,
    val vehicle_id: Int?,
    val vehicle_license_number: String?,
    val vehicle_model: String?,
    val vehicle_name: String?,
    val vehicle_number: String?,
    val vehicle_type_id: Int?,
    val vehicle_year: String?
):Parcelable















