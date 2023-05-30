package com.bayride.presentation.features.changePassword

import com.bayride.data.datasources.remote.entities.ResponsePassword


data class ChangePasswordViewState(val responsePassword: ResponsePassword? = null,val loading:Boolean? =false)
