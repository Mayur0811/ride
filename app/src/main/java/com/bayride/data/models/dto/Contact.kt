package com.bayride.data.models.dto

import android.graphics.Bitmap

data class Contact(
    var name: String = "",
    var mobileNumber: String = "",
    var filteredNumber: String = "",
    var photo: Bitmap? = null,
    var photoURI: String? = null,
    var checked: Boolean = false
)
