package com.bayride.presentation.features.driver.driverOngoing

import com.bayride.data.datasources.remote.entities.BookingHistoryInfo
import com.bayride.data.models.dto.DriverOngoingFairDetails

data class DriverOngoingScreenViewState(
    val loading: Boolean? = false,
    val bookingHistoryInfo: BookingHistoryInfo? = null,
    val driverOngoingFairDetails: DriverOngoingFairDetails? = null
)


