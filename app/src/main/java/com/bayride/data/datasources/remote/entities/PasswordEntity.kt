package com.bayride.data.datasources.remote.entities

import kotlinx.serialization.Serializable

@Serializable
data class ResponsePassword(
    val Status: Int?,
    val Message: String?
)