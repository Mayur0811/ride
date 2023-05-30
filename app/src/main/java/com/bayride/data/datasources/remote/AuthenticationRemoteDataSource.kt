package com.bayride.data.datasources.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.data.datasources.remote.api.ApiService
import com.bayride.data.datasources.remote.entities.*
import com.bayride.data.models.dto.Logout
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject


class AuthenticationRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    private val secureStorageManager: SecureStorageManager,
    @ApplicationContext private val context: Context
) : IAuthenticationRemoteDataSource {

    override fun login(
        email: String,
        password: String,
        user_lat: Double,
        user_long: Double,
        device_token: String,
        device_type: Int,
        device_id: String
    ): Single<SignInResponse> {
        val request = SignInRequest(
            email,
            password,
            user_lat,
            user_long,
            device_token,
            device_type,
            device_id
        )
        return apiService.login(request)
    }

    override fun signUpEmail(
        user_role: Int,
        email: String,
        device_token: String,
        device_type: Int,
        device_id: String
    ): Single<SignUpEmailResponse> {
        return apiService.signUpEmail(
            SignUpRequest(
                user_role,
                email,
                device_token,
                device_type,
                device_id
            )
        )
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
    ): Single<SigUpResponse> {

        val body: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        if (user_phone_number != null && country_code != null) {
            body.addFormDataPart("user_phone_number", user_phone_number)
                .addFormDataPart("country_code", country_code)
        }
        if (user_name != null) {
            body.addFormDataPart("user_name", user_name.toString())
        }
        if (user_first_name != null) {
            body.addFormDataPart("user_first_name", user_first_name.toString())
        }
        if (user_address != null) {
            body.addFormDataPart("user_address", user_address.toString())
        }
        if (user_password != null) {
            body.addFormDataPart("user_password", user_password.toString())
        }
        if (user_profile_pic != null) {
            body.addFormDataPart(
                "user_profile_pic",
                user_profile_pic.name,
                RequestBody.create(MultipartBody.FORM, getBitmap(user_profile_pic).toByteArray())
            ).addFormDataPart("no_user_profile_pic", "1")
        }

        body.addFormDataPart("user_lat", secureStorageManager.latitude)
        body.addFormDataPart("user_long", secureStorageManager.longitude)

        if (user_signature != null) {
            body.addFormDataPart(
                "user_signature",
                user_signature?.getName(),
                RequestBody.create(MultipartBody.FORM, getBitmap(user_signature).toByteArray())
            )
        }
        if (is_accept_pets != null) {
            body.addFormDataPart("is_accept_pets", is_accept_pets.toString())
        }
        if (is_crypto != null) {
            body.addFormDataPart("is_crypto", is_crypto.toString())
        }


//        .addFormDataPart("user_phone_number", user_phone_number)
//            .addFormDataPart("user_name", user_name.toString())
//            .addFormDataPart("user_first_name", user_first_name.toString())
//            .addFormDataPart("user_address", user_address.toString())
//            .addFormDataPart("user_password", user_password.toString())
//            .addFormDataPart("user_lat", "20.141214")
//            .addFormDataPart("user_long", "72.121212")
//            .addFormDataPart(
//                "user_signature", user_signature?.name.toString(), RequestBody.create(
//                    "multipart/form-data".toMediaTypeOrNull(),
//                    user_signature.toString()
//                )
//            )
//            .addFormDataPart("is_accept_pets", is_accept_pets.toString())
//            .addFormDataPart("is_crypto", is_crypto.toString())
//            .addFormDataPart("no_user_profile_pic","1")

//        val body: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)
//            .addFormDataPart(
//                "user_profile_pic",
//                user_profile_pic?.getName(),
//                RequestBody.create(MultipartBody.FORM, getBitmap(user_profile_pic).toByteArray())
//            ).addFormDataPart("no_user_profile_pic", "1")
//
//        var sign: MultipartBody.Builder? = null
//        if (user_signature != null) {
//            sign = MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart(
//                    "user_signature",
//                    user_signature?.getName(),
//                    RequestBody.create(MultipartBody.FORM, getBitmap(user_signature).toByteArray())
//                ).addFormDataPart("no_user_profile_pic", "1")
//        }
        val requestProfile: RequestBody? = user_profile_pic?.path?.let {
            RequestBody.create(
                "application/octet-stream".toMediaTypeOrNull(),
                it
            )
        }
        val requestFile: RequestBody? = user_signature?.path?.let {
            RequestBody.create(
                "application/octet-stream".toMediaTypeOrNull(),
                it
            )
        }
        val signature: MultipartBody.Part? =
            requestFile?.let {
                MultipartBody.Part.createFormData(
                    "user_signature",
                    user_signature.name,
                    it
                )
            }
        val profile: MultipartBody.Part? = requestProfile?.let {
            MultipartBody.Part.createFormData(
                "user_profile_pic",
                user_profile_pic.name, it
            )
        }

        return apiService.signUpEdit(
            body.build()
        )
    }

//    override fun signUpEdit(
//        user_phone_number: String?,
//        user_name: String,
//        user_first_name: String,
//        user_address: String?,
//        user_password: String,
//        user_profile_pic: File?,
//        user_lat: Double,
//        user_long: Double,
//        user_signature: File?,
//        is_accept_pets: Int?,
//        is_crypto: Int?
//    ): Single<SigUpResponse> {
//
//    }

    override fun changePassword(user_password: String, new_pass: String): Single<ResponsePassword> {
        return apiService.changePassword(user_password, new_pass)
    }

    override fun forgotPassword(email: String): Single<ResponsePassword> {
        return apiService.forgotPassword(email)
    }

    override fun resetPassword(
        user_email_id: String?,
        temp_pass: String,
        new_pass: String
    ): Single<ResponsePassword> {
        return apiService.resetPassword(
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

        val requestFile: RequestBody? = contact_profile_pic?.path?.let {
            RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                it
            )
        }
        val contactProfilePic: MultipartBody.Part? =
            requestFile?.let {
                MultipartBody.Part.createFormData(
                    "file",
                    contact_profile_pic.name,
                    it
                )
            }
        return apiService.addEmergencyContact(
            contact_name.toString().toRequestBody(),
            contact_phone_number.toString().toRequestBody(),
            contactProfilePic,
            no_contact_profile_pic.toString().toRequestBody()
        )
    }

    override fun listEmergencyContact(): Single<EmergencyContact> {
        return apiService.listEmergencyContact()
    }

    override fun deleteEmergencyContact(emergency_contact_id: Int): Single<ResponsePassword> {
        return apiService.deleteEmergencyContact(emergency_contact_id)
    }

    override fun logout(deviceId: String?): Single<Logout> {
        return apiService.logout(deviceId)
    }

    fun getBitmap(file: File?): ByteArrayOutputStream {
        val bmp: Bitmap = BitmapFactory.decodeFile(file?.absolutePath)
        val bos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, bos)
        return bos
    }

}