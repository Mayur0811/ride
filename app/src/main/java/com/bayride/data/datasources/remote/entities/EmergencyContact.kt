package com.bayride.data.datasources.remote.entities

import kotlinx.serialization.Serializable

@Serializable
data class EmergencyContact(
    val Message: String? = null,
    val Status: Int? = null,
    val info: List<EmergencyContactInfo>? = null
)

@Serializable
data class EmergencyContactInfo(
    val contact_created_at: String?,
    val contact_is_active: Int?,
    val contact_name: String?,
    val contact_phone_number: String?,
    val contact_profile_pic: String?,
    val contact_updated_at: String?,
    val emergency_contact_id: Int?,
    val user_id: Int?
)