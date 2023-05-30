package com.bayride.presentation.features.creditCardPayment

import com.bayride.presentation.features.driver.addVehicleDetails.VehicleDetailsEvent

sealed class CreditCardEvent

object CreditCardSuccess : CreditCardEvent()
data class CreditCardErrorEvent(val error: Int, val Message: String) : CreditCardEvent()
data class CreditCardFailedEvent(val error: Throwable) : CreditCardEvent()
