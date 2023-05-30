package com.bayride.presentation.features.driver.onGoingScreendialog

import com.bayride.presentation.features.driver.driverOngoing.DriverOnGoingScreenEvent

sealed class OnGoingBottomSheetEvent

object OnGoingBottomSheetSuccessEvent : OnGoingBottomSheetEvent()
object PickupCompleteByDriverSuccessEvent : OnGoingBottomSheetEvent()
data class OnGoingBottomSheetFailedEvent(val error: Throwable) : OnGoingBottomSheetEvent()
data class OnGoingBottomSheetErrorEvent(val code: Int, val Message: String) :
    OnGoingBottomSheetEvent()
