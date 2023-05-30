package com.bayride.data.models.dto

import android.graphics.Bitmap
import android.icu.text.CaseMap
import android.net.Uri

data class Passenger(val title: String, val fieldName: String, val hint: String)

data class PassengerSignup(
    var photo: String? = null,
    var name: String? = null,
    var username: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,
    var createPassword: Pair<String, String>? = null,
    var signature: String? = null,
    var enterPaymentDetails: Boolean? = null,
    var country_code: String? = null
)
