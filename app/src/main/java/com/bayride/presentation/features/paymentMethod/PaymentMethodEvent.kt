package com.bayride.presentation.features.paymentMethod

import com.bayride.data.datasources.remote.entities.SigUpResponse
import com.bayride.data.datasources.remote.entities.SignUpEmailResponse
import com.bayride.presentation.features.login.LoginEvent

sealed class PaymentMethodEvent

data class PaymentMethodSuccessEvent(val signUpResponse: SigUpResponse? = null) :
    PaymentMethodEvent()

data class PaymentMethodErrorEvent(val code: Int, val errorMessage: String) : PaymentMethodEvent()
data class PaymentMethodFailEvent(val error: Throwable) : PaymentMethodEvent()
