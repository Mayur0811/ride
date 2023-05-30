package com.bayride.presentation.features.bookingHistory

import com.bayride.data.datasources.remote.entities.BookingHistoryEntity

data class BookingHistoryState(
    val loading: Boolean? = false,
    val bookingHistoryEntity: BookingHistoryEntity? = null,
    val user_role: Int? = null
)