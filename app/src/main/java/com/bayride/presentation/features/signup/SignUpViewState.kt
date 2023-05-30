package com.bayride.presentation.features.signup

import android.provider.ContactsContract
import com.bayride.data.models.dto.PassengerSignup

data class SignUpViewState(
    val passenger: PassengerSignup? = null,
    val email: String? = null,
    val loading: Boolean? = false
)