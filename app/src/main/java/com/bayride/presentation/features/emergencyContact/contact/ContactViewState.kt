package com.bayride.presentation.features.emergencyContact.contact

import com.bayride.data.models.dto.Contact

data class ContactViewState(
    val contactList: List<Contact>? = null ,
    val loading: Boolean? = true
)
