package com.bayride.presentation.features.driver.insuranceInformation

import com.bayride.presentation.features.emergencyContact.AddEmergencyContactEvent

sealed class InsurancesInformationEvent

object InsurancesInformationSuccessEvent : InsurancesInformationEvent()
data class InsurancesInformationFailedEvent(val error: Throwable) : InsurancesInformationEvent()
data class InsurancesInformationErrorEvent(val code: Int, val Message: String) : InsurancesInformationEvent()