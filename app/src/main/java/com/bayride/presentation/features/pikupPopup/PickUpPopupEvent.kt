package com.bayride.presentation.features.pikupPopup

import com.bayride.presentation.features.ongoing.OngoingEvent

sealed class PickUpPopupEvent

object PickUpPopupSuccessEvent : PickUpPopupEvent()
object FairCompleteUserSuccessEvent : PickUpPopupEvent()

data class PickUpPopupErrorEvent(val code: Int, val Message: String) : PickUpPopupEvent()
data class PickUpPopupFailEvent(val error: Throwable) : PickUpPopupEvent()
