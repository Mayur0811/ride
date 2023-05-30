package com.bayride.data.datasources.remote.entities

import android.net.Uri
import kotlinx.serialization.Serializable


@Serializable
data class  SigUpResponse(
    val Message: String? =null,
    val Status: Int? =null,
    val info: Info? =null
)

@Serializable
data class SignUpEmailResponse(
    val Message: String? = null,
    val Status: Int? = null,
    val UserToken: String? = null,
    val info: SignUpEmailInfo? = null
)

@Serializable
data class Info(
    val created_at: String?,
    val temp_pass: String?,
    val user_active: Int,
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
    val user_terms_condition: String?
)

@Serializable
data class SignUpRequest(
    val user_role: Int?,
    val user_email_id: String?,
    val device_token: String?,
    val device_type: Int,
    val device_id: String
)


data class SignUpEditRequest(
    val user_id: Int,
    val user_phone_number: String?,
    val user_name: String,
    val user_password: String,
    val user_profile_pic: Uri?,
    val user_email_id: String,
    val user_lat: Double,
    val user_long: Double,
)

@Serializable
data class SignUpEmailInfo(
    val created_at: String? = null,
    val device_id: String? = null,
    val device_token: String? = null,
    val device_type: Int? = null,
    val temp_pass: String? = null,
    val token_id: Int? = null,
    val user_active: Int? = null,
    val user_address: String? = null,
    val user_block: Int? = null,
    val user_city: String? = null,
    val user_country: String? = null,
    val user_email_id: String? = null,
    val user_email_verified: Int? = null,
    val user_first_name: String? = null,
    val user_id: Int? = null,
    val user_last_name: String? = null,
    val user_lat: String? = null,
    val user_long: String? = null,
    val user_name: String? = null,
    val user_password: String? = null,
    val user_phone_number: String? = null,
    val user_phone_number_verified: Int? = null,
    val user_profile_pic: String? = null,
    val user_role: Int? = null,
    val user_signature: String? = null,
    val user_state: String? = null,
    val user_terms_condition: String? = null
)
enum class SelectionScreenInputType {
    TEXT, NUMBER, PASSWORD
}

