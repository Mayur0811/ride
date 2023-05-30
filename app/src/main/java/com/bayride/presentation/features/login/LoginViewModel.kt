package com.bayride.presentation.features.login

import com.bayride.common.reactive.applyIO
import com.bayride.common.sharedpreference.saveModelObjectToSharedPreference
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.memory.IMemoryDataStorage
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val secureStorageManager: SecureStorageManager,
    private val iMemoryDataStorage: IMemoryDataStorage
) :
    BaseViewModel<LoginState, LoginEvent>() {

    override fun initState(): LoginState {
        return LoginState()
    }

    fun login(
        email: String,
        password: String,
        user_lat: Double,
        user_long: Double,
        device_token: String,
        device_type: Int,
        device_id: String
    ) {
        dispatchState(currentState.copy(loading = true))
        authenticationRepository.login(
            email,
            password,
            user_lat,
            user_long,
            device_token,
            device_type,
            device_id
        ).applyIO()
            .doFinally { dispatchState(currentState.copy(loading = false)) }
            .subscribe({
                iMemoryDataStorage.userLoginDetails?.let { LoginSuccessEvent(it) }
                    ?.let { dispatchEvent(it) }
            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(LoginErrorEvent(code))
                } else {
                    dispatchEvent(LoginFailEvent(exception))
                }
            })

    }
}