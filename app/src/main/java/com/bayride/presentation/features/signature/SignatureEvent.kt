package com.bayride.presentation.features.signature

import com.bayride.data.datasources.remote.entities.SigUpResponse
import com.bayride.presentation.features.uploadphoto.PhotoUploadEvent

sealed class SignatureEvent

data class SignatureSuccessEvent(val signUpResponse: SigUpResponse? = null) : SignatureEvent()

data class SignatureError(val code: Int,val Message:String) : SignatureEvent()
data class SignatureFailed(val throwable: Throwable) : SignatureEvent()