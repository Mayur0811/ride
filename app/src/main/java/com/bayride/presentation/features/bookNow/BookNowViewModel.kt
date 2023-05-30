package com.bayride.presentation.features.bookNow

import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.repositories.passenger.PassengerRepository
import com.bayride.presentation.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookNowViewModel @Inject constructor(
    private val passengerRepository: PassengerRepository,
    val secureStorageManager: SecureStorageManager
) : BaseViewModel<BookNowViewState, BookNowEvent>() {
    override fun initState(): BookNowViewState {
        return BookNowViewState()
    }

    fun loadData(args: BookNowBottomSheetArgs) {
        dispatchState(currentState.copy(fairList = args.fairList))
        dispatchState(currentState.copy(driverRequestInfo = args.driverInfo))
        secureStorageManager.driverID = args.driverInfo.driver_bid_by ?: 0
    }
}