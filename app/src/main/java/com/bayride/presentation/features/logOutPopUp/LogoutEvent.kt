package com.bayride.presentation.features.logOutPopUp

import com.bayride.presentation.features.addInformation.AddFairEvent

sealed class LogoutEvent

object LogoutSuccessEvent : LogoutEvent()
data class LogoutErrorEvent(val error: Int, val Message: String) : LogoutEvent()
data class LogoutFailedEvent(val error: Throwable) : LogoutEvent()