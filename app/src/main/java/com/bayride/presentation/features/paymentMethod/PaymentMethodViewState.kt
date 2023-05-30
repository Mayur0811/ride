package com.bayride.presentation.features.paymentMethod

import com.bayride.data.datasources.remote.entities.SigUpResponse
import com.bayride.data.datasources.remote.entities.SignUpEmailResponse

data class PaymentMethodViewState(
    val loading: Boolean = false,
    val passengerDetails: SigUpResponse? = null
)
