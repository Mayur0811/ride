package com.bayride.presentation.features.emergencyContact.contact

sealed class ContactEvent

object ContactGetSuccessFully : ContactEvent()

data class ContactErrorEvent(val code: Int, val Message: String) : ContactEvent()
data class ContactFailedEvent(val error: Throwable) : ContactEvent()