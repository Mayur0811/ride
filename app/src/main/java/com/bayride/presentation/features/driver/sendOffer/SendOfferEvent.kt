package com.bayride.presentation.features.driver.sendOffer

import com.bayride.presentation.features.driver.addVehicleDetails.VehicleDetailsEvent

sealed class SendOfferEvent

object SendOfferSuccessFully : SendOfferEvent()
object AcceptRequestSuccessByDriver : SendOfferEvent()
object FairListSuccessEvent:SendOfferEvent()
data class SendOfferErrorEvent(val error: Int, val Message: String) : SendOfferEvent()
data class SendOfferFailedEvent(val error: Throwable) : SendOfferEvent()