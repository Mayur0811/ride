package com.bayride.data.repositories.authentication

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.util.Patterns
import com.bayride.common.views.isValidEmail
import com.bayride.data.datasources.local.IPreferenceStorage
import com.bayride.data.datasources.local.ISecureStorageManager
import com.bayride.data.datasources.memory.IMemoryDataStorage
import com.bayride.data.datasources.remote.AuthenticationRemoteDataSource
import com.bayride.data.datasources.remote.entities.EmergencyContact
import com.bayride.data.datasources.remote.entities.ResponsePassword
import com.bayride.data.models.dto.Logout
import com.bayride.data.models.exceptions.EmptyEmailException
import com.bayride.data.models.exceptions.EmptyPasswordException
import com.bayride.data.models.exceptions.InvalidEmailException
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.File
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationRemoteDataSource: AuthenticationRemoteDataSource,
    private val secureStorageManager: ISecureStorageManager,
    private val preferenceStorage: IPreferenceStorage,
    private val memoryDataStorage: IMemoryDataStorage,
    @ApplicationContext context: Context
) : AuthenticationRepository {
    override fun login(
        email: String,
        password: String,
        user_lat: Double,
        user_long: Double,
        device_token: String,
        device_type: Int,
        device_id: String
    ): Completable {


        if (email.isEmpty()) return Completable.error(EmptyEmailException())

        if (!isValidEmail(email)) return Completable.error(InvalidEmailException())

        if (!validatePassword(password)) return Completable.error(EmptyPasswordException())

        return authenticationRemoteDataSource.login(
            email,
            password,
            user_lat,
            user_long,
            device_token,
            device_type,
            device_id
        )
            .flatMapCompletable { response ->
                secureStorageManager.token = response.UserToken.toString()
                secureStorageManager.userID = response.info?.user_id!!
                secureStorageManager.userEmail = response.info.user_email_id.toString()
                secureStorageManager.user_role = response.info.user_role ?: 0
                memoryDataStorage.userLoginDetails = response
                Log.d(TAG, "login: $response")
                Completable.complete()
            }
    }

    override fun signUpEmail(
        user_role: Int,
        email: String,
        device_token: String,
        device_type: Int,
        device_id: String
    ): Completable {

        if (email.isEmpty()) return Completable.error(EmptyEmailException())

        if (!validateEmail(email)) return Completable.error(InvalidEmailException())

        return authenticationRemoteDataSource.signUpEmail(
            user_role,
            email,
            device_token,
            device_type,
            device_id
        ).flatMapCompletable { response ->
            //   secureStorageManager.token = response.Message
            // secureStorageManager.token = response.info.user
            secureStorageManager.token = response.UserToken.toString()
            memoryDataStorage.signupUserDetails = response
            Completable.complete()
        }
    }

    override fun signUpEdit(
        user_phone_number: String?,
        user_name: String?,
        user_first_name: String?,
        user_address: String?,
        user_password: String?,
        user_profile_pic: File?,
        user_lat: Double?,
        user_long: Double?,
        user_signature: File?,
        is_accept_pets: Int?,
        is_crypto: Int?,
        country_code: String?
    ): Completable {
        return authenticationRemoteDataSource.signUpEdit(
            user_phone_number,
            user_name,
            user_first_name,
            user_address,
            user_password,
            user_profile_pic,
            user_lat,
            user_long,
            user_signature,
            is_accept_pets,
            is_crypto,
            country_code
        ).flatMapCompletable { response ->
            memoryDataStorage.signupDetails = response
            Completable.complete()
        }
    }

//    override fun signUpEdit(
//        user_phone_number: String,
//        user_name: String,
//        user_first_name: String,
//        user_address:String?,
//        user_password: String,
//        user_profile_pic: File?,
//        user_lat: Double,
//        user_long: Double,
//        user_signature: File?,
//        is_accept_pets: Int?,
//        is_crypto: Int?
//    ): Completable {
//        return authenticationRemoteDataSource.signUpEdit(
//            user_phone_number,
//            user_name,
//            user_first_name,
//            user_address,
//            user_password,
//            user_profile_pic,
//            user_lat,
//            user_long,
//            user_signature,
//            is_accept_pets,
//            is_crypto
//        ).flatMapCompletable { response ->
//            memoryDataStorage.signupDetails = response
//            Completable.complete()
//        }
//    }

    override fun changePassword(
        user_password: String,
        new_pass: String,
    ): Single<ResponsePassword> {
        return authenticationRemoteDataSource.changePassword(user_password, new_pass)
    }

    //    override fun signUpEdit(
//        user_id: Int,
//        user_phone_number: String?,
//        user_name: String,
//        user_password: String,
//        user_profile_pic: Uri,
//        user_email_id: String,
//        user_lat: Double,
//        user_long: Double
//    ): Completable {
//
//    }
    override fun forgotPassword(email: String): Single<ResponsePassword> {
        return authenticationRemoteDataSource.forgotPassword(email)
    }

    override fun resetPassword(
        user_email_id: String?,
        temp_pass: String,
        new_pass: String
    ): Single<ResponsePassword> {
        return authenticationRemoteDataSource.resetPassword(
            user_email_id,
            temp_pass,
            new_pass
        )
    }

    override fun addEmergencyContact(
        contact_name: String?,
        contact_phone_number: String?,
        contact_profile_pic: File?,
        no_contact_profile_pic: String?
    ): Single<ResponsePassword> {
        return authenticationRemoteDataSource.addEmergencyContact(
            contact_name,
            contact_phone_number,
            contact_profile_pic,
            no_contact_profile_pic
        )
    }

    override fun listEmergencyContact(): Single<EmergencyContact> {
        return authenticationRemoteDataSource.listEmergencyContact()
    }

    override fun deleteEmergencyContact(emergency_contact_id: Int): Single<ResponsePassword> {
        return authenticationRemoteDataSource.deleteEmergencyContact(emergency_contact_id)
    }

    override fun logout(deviceId: String?): Single<Logout> {
        return authenticationRemoteDataSource.logout(deviceId)
    }

    private fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.isNotEmpty()
    }

    private fun strongPassword(password: String): Boolean {
        return password.matches(Regex((".*\\d.*")))
    }

    private fun validateFullName(fullName: String): Boolean {
        return fullName.isNotEmpty()
    }

}