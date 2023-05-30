package com.bayride.data.models.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class AddFairResponse(
    val Message: String,
    val Status: Int,
    val info: AddFairInfo
)

@Serializable
data class AddFairInfo(
    val fare_id: Int
)