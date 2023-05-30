package com.bayride.data.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class ListFairUser(
    val Message: String?,
    val Status: Int?,
    val info: List<ListFairUserInfo>?
)

@Serializable
data class ListFairUserInfo(
    val country_code: String?,
    val created_at: String?,
    val fare_bidding_time: String?,
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
    val is_accept_pets: Int?,
    val is_active: Int?,
    val is_confirmed_by: Int?,
    val is_crypto: Int?,
    val new_distance: Double?,
    val payment_method_type: Int?,
    val pickup_otp: Int?,
    val temp_pass: Int?,
    val to_address: String?,
    val to_city: String?,
    val to_country: String?,
    val to_lat: Double?,
    val to_long: Double?,
    val updated_at: String?,
    val user_active: Int?,
    val user_address: String?,
    val user_block: Int?,
    val user_city: String?,
    val user_country: String?,
    val user_email_id: String?,
    val user_email_verified: Int?,
    val user_first_name: String?,
    val user_id: Int?,
    val user_last_name: String?,
    val user_lat: Double?,
    val user_long: Double?,
    val user_name: String?,
    val user_password: String?,
    val user_phone_number: String?,
    val user_phone_number_verified: Int?,
    val user_profile_pic: String?,
    val user_role: Int?,
    val user_signature: String?,
    val user_state: String?,
    val user_terms_condition: String?,
    val vehicle_type_id: Int?
)