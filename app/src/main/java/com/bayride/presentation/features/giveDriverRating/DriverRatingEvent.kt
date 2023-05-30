package com.bayride.presentation.features.giveDriverRating

import com.bayride.presentation.features.driver.sendOffer.SendOfferEvent

sealed class DriverRatingEvent

object DriverRatingSuccessFully : DriverRatingEvent()
    object DriverProfileSuccessEvent : DriverRatingEvent()
data class DriverRatingErrorEvent(val error: Int, val Message: String) : DriverRatingEvent()
data class DriverRatingFailedEvent(val error: Throwable) : DriverRatingEvent()