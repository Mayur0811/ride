package com.bayride.presentation.features.driver.driverBookingHistory

import com.bayride.data.datasources.remote.entities.BookingHistoryEntity

sealed class DriverBookingHistoryEvent

data class DriverBookingHistorySuccessEvent(var bookingHistoryEntity: BookingHistoryEntity): DriverBookingHistoryEvent()
data class DriverBookingHistoryErrorEvent(val errorCode: Int,val errorMessage: String) : DriverBookingHistoryEvent()
data class DriverBookingHistoryFailEvent(val error: Throwable) : DriverBookingHistoryEvent()