package com.bayride.data.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class Logout(
    val Message: String?,
    val Status: Int?
)