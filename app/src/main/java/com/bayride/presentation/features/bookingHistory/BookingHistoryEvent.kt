package com.bayride.presentation.features.bookingHistory

import com.bayride.data.datasources.remote.entities.BookingHistoryEntity

sealed class BookingHistoryEvent

data class BookingHistorySuccessEvent(var bookingHistoryEntity: BookingHistoryEntity): BookingHistoryEvent()
data class BookingHistoryErrorEvent(val errorCode: Int,val errorMessage: String) : BookingHistoryEvent()
data class BookingHistoryFailEvent(val error: Throwable) : BookingHistoryEvent()