package com.bayride.presentation.features.driver.otpdialog

import com.bayride.presentation.features.driver.onGoingScreendialog.OnGoingBottomSheetEvent

sealed class PickupOtpEvent

object PickupOtpSuccessEvent : PickupOtpEvent()
data class PickupOtpFailedEvent(val error: Throwable) : PickupOtpEvent()
data class PickupOtpErrorEvent(val code: Int, val Message: String) :
    PickupOtpEvent()