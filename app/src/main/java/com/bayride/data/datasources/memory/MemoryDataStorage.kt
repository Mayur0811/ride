package com.bayride.data.datasources.memory

import com.bayride.data.datasources.remote.entities.SigUpResponse
import com.bayride.data.datasources.remote.entities.SignInResponse
import com.bayride.data.datasources.remote.entities.SignUpEmailResponse
import javax.inject.Inject

interface IMemoryDataStorage {
    var userID: Int

    var signupUserDetails: SignUpEmailResponse?
    var signupDetails: SigUpResponse?

    var userLoginDetails: SignInResponse?

    fun clearAll()
}

class MemoryDataStorage @Inject constructor() : IMemoryDataStorage {
    override var signupUserDetails: SignUpEmailResponse? = null
    override var signupDetails: SigUpResponse? = null


    override var userLoginDetails: SignInResponse? = null

    override var userID: Int = -1

    override fun clearAll() {
        userID = -1
    }
}