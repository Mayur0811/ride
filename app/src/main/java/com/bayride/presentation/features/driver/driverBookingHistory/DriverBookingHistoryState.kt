package com.bayride.presentation.features.driver.driverBookingHistory

import com.bayride.data.datasources.remote.entities.BookingHistoryEntity

data class DriverBookingHistoryState(
    val loading: Boolean? = false,
    val bookingHistoryEntity: BookingHistoryEntity? = null,
    val user_role: Int? = null
)