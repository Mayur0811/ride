package com.bayride.presentation.features.emergencyContact

import com.bayride.data.datasources.remote.entities.EmergencyContactInfo

data class AddEmergencyContactViewState(
    val loading: Boolean? = false,
    val contactList: List<EmergencyContactInfo>? = null
)