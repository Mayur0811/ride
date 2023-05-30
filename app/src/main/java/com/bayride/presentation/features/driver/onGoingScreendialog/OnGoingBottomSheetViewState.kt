package com.bayride.presentation.features.driver.onGoingScreendialog

import com.bayride.data.datasources.remote.entities.BookingHistoryInfo
import com.bayride.data.datasources.remote.entities.ResponsePassword
import com.bayride.data.models.dto.DriverOngoingFairDetails

data class OnGoingBottomSheetViewState(
    val loading: Boolean? = false,
    val bookingHistoryInfo: BookingHistoryInfo? = null,
    val driverOngoingFairDetails: DriverOngoingFairDetails? = null,
    val response: ResponsePassword? = null,
    val driverFairId: Int? = null
)