package com.bayride.data.datasources.remote.entities

import kotlinx.serialization.Serializable

@Serializable
data class ProfileEntity(
    val Message: String? =null,
    val Status: Int? =null,
    val info: DriverProfileInfo? =null
)

@Serializable
data class DriverProfileInfo(
    val driver_info: List<DriverInfo>?=null
)

@Serializable
data class DriverInfo(
    val created_at: String?,
    val insurance_info: List<InsuranceInfo>?=null,
    val is_accept_pets: Int?,
    val is_crypto: Int?,
    val real_star: Int?,
    val total_review: Int?,
    val updated_at: String?,
    val user_first_name: String?,
    val user_last_name: String?,
    val user_name: String?,
    val user_phone_number: String?,
    val user_profile_pic: String?,
    val vehicle_added_by: Int?,
    val vehicle_brand: String?,
    val vehicle_colour: String?,
    val vehicle_id: Int?,
    val vehicle_is_active: Int?,
    val vehicle_license_number: String?,
    val vehicle_model: String?,
    val vehicle_name: String?,
    val vehicle_number: String?,
    val vehicle_type_id: Int?,
    val vehicle_type_image: String?,
    val vehicle_type_name: String?,
    val vehicle_year: String?
)

@Serializable
data class InsuranceInfo(
    val driver_option_name: String?,
    val driver_option_value: String?
)