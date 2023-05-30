package com.bayride.data.datasources.remote.entities

import android.provider.ContactsContract
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class SignInResponse(
    val Message: String? = null,
    val Status: Int? = null,
    val UserToken: String? = null,
    val info: LoginInfo? = null
)

@Serializable
data class SignInRequest(
    val user_email_id: String?,
    val user_password: String?,
    val user_lat: Double?,
    val user_long: Double?,
    val device_token: String?,
    val device_type: Int,
    val device_id: String?
)

@Serializable
data class LoginInfo(
    val created_at: String?,
    val device_id: String?,
    val device_token: String?,
    val device_type: Int?,
    val temp_pass: String?,
    val token_id: Int?,
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
    val emergency_count: Int?
)