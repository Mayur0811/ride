package com.bayride.presentation.features.driver.acceptPets

import com.bayride.common.reactive.applyIO
import com.bayride.data.datasources.memory.IMemoryDataStorage
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.driver.bankDetails.BankDetailsSuccessEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AcceptPetsViewModel @Inject constructor(
    var authenticationRepository: AuthenticationRepository,
    val iMemoryDataStorage: IMemoryDataStorage
) :
    BaseViewModel<AcceptPetsState, AcceptPetsEvent>() {
    override fun initState(): AcceptPetsState {
        return AcceptPetsState()
    }


    fun signUpEdit(
        is_accept_pets: Int
    ) {
        dispatchState(currentState.copy(loading = true))
        authenticationRepository.signUpEdit(
            is_accept_pets = is_accept_pets
        ).applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe(
                {
                    iMemoryDataStorage.signupDetails?.let { AcceptPetsSuccessEvent(it) }
                        ?.let { dispatchEvent(it) }
                },
                { error ->
                    val errorMessage = BadRequest.parse(error)?.detail
                    val exception = errorMessage
                        ?.let { RuntimeException(errorMessage) }
                        ?: run { error }
                    if (error is HttpException) {
                        val code = error.code()
                        dispatchEvent(AcceptPetsErrorEvent(code, error.message()))
                    } else {
                        dispatchEvent(AcceptPetsFailedEvent(exception))
                    }
                },
            )
    }
}