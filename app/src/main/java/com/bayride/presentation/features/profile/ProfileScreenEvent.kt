package com.bayride.presentation.features.profile

sealed class ProfileScreenEvent

object ProfileScreenSuccessFullyEvent : ProfileScreenEvent()

data class ProfileScreenErrorEvent(val error: Int, val Message: String) : ProfileScreenEvent()
data class ProfileScreenFailedEvent(val error: Throwable) : ProfileScreenEvent()

