package com.bayride.presentation.features.emergencyContact

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.datasources.remote.entities.EmergencyContact
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.emergencyContact.contact.ContactErrorEvent
import com.bayride.presentation.features.emergencyContact.contact.ContactFailedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AddEmergencyContactViewModel @Inject constructor(val authenticationRepository: AuthenticationRepository) :
    BaseViewModel<AddEmergencyContactViewState, AddEmergencyContactEvent>() {
    override fun initState(): AddEmergencyContactViewState {
        return AddEmergencyContactViewState()
    }

    fun listEmergencyContact() {
        dispatchState(currentState.copy(loading = true))
        authenticationRepository.listEmergencyContact().applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchState(currentState.copy(contactList = it.info))
                dispatchEvent(AddEmergencyContactSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(AddEmergencyContactErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(AddEmergencyContactFailedEvent(exception))
                }
            }).addToCompositeDisposable()
    }


    fun deleteEmergencyContact(emergency_contact_id: Int) {
        authenticationRepository.deleteEmergencyContact(emergency_contact_id)
            .applyIO()
            .doFinally { }
            .subscribe({
                dispatchEvent(DeleteEmergencyContactSuccessEvent)
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(AddEmergencyContactErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(AddEmergencyContactFailedEvent(exception))
                }
            }).addToCompositeDisposable()
    }
}