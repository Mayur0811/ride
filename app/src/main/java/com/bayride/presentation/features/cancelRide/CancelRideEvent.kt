package com.bayride.presentation.features.cancelRide

sealed class CancelRideEvent

object CancelRideSuccessEvent : CancelRideEvent()
data class CancelRideErrorEvent(val Code: Int, val Message: String) : CancelRideEvent()
data class CancelRideFailEvent(val error: Throwable) : CancelRideEvent()