package com.bayride.data.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class EditDriverOption(
    val Message: String,
    val Status: Int,
    val info: EditDriverOptionInfo
)

@Serializable
data class EditDriverOptionInfo(
    val created_at: String,
    val driver_option_answer_id: Int,
    val driver_option_id: Int,
    val driver_option_value: String,
    val user_id: Int
)