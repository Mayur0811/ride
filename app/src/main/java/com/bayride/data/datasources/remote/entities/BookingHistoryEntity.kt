package com.bayride.data.datasources.remote.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class BookingHistoryEntity(
    val Message: String,
    val Status: Int,
    val info: List<BookingHistoryInfo>
) : Parcelable

@Serializable
@Parcelize
data class BookingHistoryInfo(
    val booked_at: String?,
    val created_at: String?,
    val fare_bidding_time: String?,
    val fare_booking_amount: String?,
    val fare_booking_unique_id: String?,
    val fare_comments: String?,
    val fare_cost_by_user: String?,
    val fare_id: Int?,
    val fare_status: Int?,
    val fare_terms_condition: String?,
    val from_address: String?,
    val from_city: String?,
    val from_country: String?,
    val from_lat: Double?,
    val from_long: Double?,
    val is_active: Int?,
    val payment_method_type: Int?,
    val pickup_otp: Int?,
    val real_star: Int? = null,
    val to_address: String?,
    val to_city: String?,
    val to_country: String?,
    val to_lat: Double?,
    val to_long: Double?,
    val total_review: Int? = null,
    val updated_at: String?,
    val user_first_name: String?,
    val user_id: Int?,
    val user_last_name: String?,
    val user_name: String?,
    val user_phone_number: String?,
    val user_profile_pic: String?,
    val vehicle_type_id: Int?
) : Parcelable