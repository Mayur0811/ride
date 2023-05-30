package com.bayride.presentation.features.uploadSelfie

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.local.ISecureStorageManager
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.models.dto.FairList
import com.bayride.data.repositories.passenger.PassengerRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.uploadphoto.PhotoUploadError
import com.bayride.presentation.features.uploadphoto.PhotoUploadFailed
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadSelfieViewModel @Inject constructor(
    val passengerRepository: PassengerRepository,
    val secureStorageManager: ISecureStorageManager,

    ) :
    BaseViewModel<UploadSelfieViewState, UploadSelfieEvent>() {
    override fun initState(): UploadSelfieViewState {
        return UploadSelfieViewState()
    }

    fun requestSend() {
        dispatchState(currentState.copy(loading = true))

    }


    fun addSelfieSignature(
        fare_image_type: Int?,
        fare_image_url: File?
    ) {
        dispatchState(currentState.copy(loading = true))
        passengerRepository.addSelfieSignature(
            fare_image_type,
            secureStorageManager.fairId,
            fare_image_url
        ).applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchEvent(UploadSelfieSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(UploadSelfieErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(UploadSelfieFailedEvent(exception))
                }
            })
    }

    fun getFairList() {
        passengerRepository.getFairList(secureStorageManager.fairId)
            .applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchState(currentState.copy(fairList = it))
                dispatchEvent(UploadSelfieSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(UploadSelfieErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(UploadSelfieFailedEvent(exception))
                }
            })
    }


}