package com.bayride.presentation.features.ongoing

import com.bayride.data.datasources.remote.entities.SigUpResponse
import com.bayride.presentation.features.paymentMethod.PaymentMethodEvent

sealed class OngoingEvent

object OngoingSuccessEvent : OngoingEvent()
object DriverProfileSuccessEvent : OngoingEvent()
object GetContactSuccessEvent : OngoingEvent()
object PickupDoneByUserSuccessEvent : OngoingEvent()

data class OngoingErrorEvent(val code: Int, val errorMessage: String) : OngoingEvent()
data class OngoingFailEvent(val error: Throwable) : OngoingEvent()