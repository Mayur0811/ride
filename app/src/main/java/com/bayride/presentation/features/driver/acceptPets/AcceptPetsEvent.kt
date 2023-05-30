package com.bayride.presentation.features.driver.acceptPets

import com.bayride.data.datasources.remote.entities.SigUpResponse

sealed class AcceptPetsEvent

data class AcceptPetsSuccessEvent(val sigUpResponse: SigUpResponse) : AcceptPetsEvent()
data class AcceptPetsFailedEvent(val error: Throwable) : AcceptPetsEvent()
data class AcceptPetsErrorEvent(val code: Int, val Message: String) : AcceptPetsEvent()