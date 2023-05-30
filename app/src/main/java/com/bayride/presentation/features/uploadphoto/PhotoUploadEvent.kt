package com.bayride.presentation.features.uploadphoto

import com.bayride.data.datasources.remote.entities.SignUpEmailResponse
import com.bayride.presentation.features.signup.SignUpEvent

sealed class PhotoUploadEvent


object PhotoUploadSuccessEvent : PhotoUploadEvent()

data class PhotoUploadError(val code: Int,val Message:String) : PhotoUploadEvent()
data class PhotoUploadFailed(val throwable: Throwable) : PhotoUploadEvent()