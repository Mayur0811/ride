package com.bayride.presentation.features.emergencyContact

import java.lang.Error

sealed class AddEmergencyContactEvent

object AddEmergencyContactSuccessEvent : AddEmergencyContactEvent()
data class AddEmergencyContactFailedEvent(val error: Throwable) : AddEmergencyContactEvent()
data class AddEmergencyContactErrorEvent(val code: Int, val Message: String) :
    AddEmergencyContactEvent()

object DeleteEmergencyContactSuccessEvent : AddEmergencyContactEvent()