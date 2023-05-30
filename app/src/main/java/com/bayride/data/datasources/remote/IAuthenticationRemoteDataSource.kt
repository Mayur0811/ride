package com.bayride.data.datasources.remote

import android.net.Uri
import com.bayride.data.datasources.remote.entities.*
import com.bayride.data.models.dto.Logout
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.File

interface IAuthenticationRemoteDataSource {

    fun login(
        email: String,
        password: String,
        user_lat: Double,
        user_long: Double,
        device_token: String,
        device_type: Int,
        device_id: String
    ): Single<SignInResponse>

    fun signUpEmail(
        user_role: Int,
        email: String,
        device_token: String,
        device_type: Int,
        device_id: String
    ): Single<SignUpEmailResponse>

    fun signUpEdit(
        user_phone_number: String? = null,
        user_name: String? = null,
        user_first_name: String? = null,
        user_address: String? = null,
        user_password: String? = null,
        user_profile_pic: File? = null,
        user_lat: Double? = null,
        user_long: Double? = null,
        user_signature: File? = null,
        is_accept_pets: Int? = null,
        is_crypto: Int? = null,
        country_code: String? = null
    ): Single<SigUpResponse>

    fun changePassword(
        user_password: String,
        new_pass: String
    ): Single<ResponsePassword>


    fun forgotPassword(email: String): Single<ResponsePassword>

    fun resetPassword(
        user_email_id: String?,
        temp_pass: String,
        new_pass: String
    ): Single<ResponsePassword>


    fun addEmergencyContact(
        contact_name: String?,
        contact_phone_number: String?,
        contact_profile_pic: File?,
        no_contact_profile_pic: String?,
    ): Single<ResponsePassword>

    fun listEmergencyContact(): Single<EmergencyContact>

    fun deleteEmergencyContact(
        emergency_contact_id: Int
    ): Single<ResponsePassword>

    fun logout(deviceId: String?): Single<Logout>
}