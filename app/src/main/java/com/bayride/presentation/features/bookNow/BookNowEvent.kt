package com.bayride.presentation.features.bookNow

import com.bayride.data.datasources.remote.entities.BookingHistoryEntity
import com.bayride.presentation.features.bookingHistory.BookingHistoryEvent

sealed class BookNowEvent

object BookNowSuccessEvent : BookNowEvent()
data class BookNowErrorEvent(val Code: Int, val Message: String) : BookNowEvent()
data class BookNowFailEvent(val error: Throwable) : BookNowEvent()