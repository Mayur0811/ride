package com.bayride.presentation.features.homedetails.driverRequest

import com.bayride.presentation.features.giveDriverRating.DriverRatingEvent

sealed class DriverRequestEvent

object DriverRequestSuccessFully : DriverRequestEvent()
object UpdateRequestSuccess : DriverRequestEvent()

data class DriverRequestErrorEvent(val error: Int, val Message: String) : DriverRequestEvent()
data class DriverRequestFailedEvent(val error: Throwable) : DriverRequestEvent()
