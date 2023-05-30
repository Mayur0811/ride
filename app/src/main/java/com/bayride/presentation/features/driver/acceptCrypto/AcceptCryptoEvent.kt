package com.bayride.presentation.features.driver.acceptCrypto

import com.bayride.data.datasources.remote.entities.SignUpEmailResponse
import com.bayride.presentation.features.driver.contactInformation.ContactInformationEvent

sealed class AcceptCryptoEvent

object AcceptCryptoSuccessEvent : AcceptCryptoEvent()
data class AcceptCryptoFailedEvent(val error: Throwable) : AcceptCryptoEvent()
data class AcceptCryptoErrorEvent(val code: Int, val Message: String) : AcceptCryptoEvent()