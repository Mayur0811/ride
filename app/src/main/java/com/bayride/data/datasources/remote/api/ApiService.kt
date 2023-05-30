package com.bayride.data.datasources.remote.api

import android.net.Uri
import com.bayride.data.datasources.remote.entities.*
import com.bayride.data.models.dto.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import java.io.File

interface ApiService {

    @POST("login")
    fun login(
        @Body signInRequest: SignInRequest
    ): Single<SignInResponse>

    @POST("sign_up")
    fun signUpEmail(
        @Body signUpRequest: SignUpRequest
    ): Single<SignUpEmailResponse>


//     @Field("user_phone_number") user_phone_number: RequestBody? = null,
//        @Field("user_name") user_name: RequestBody? = null,
//        @Field("user_first_name") user_first_name: RequestBody? = null,
//        @Field("user_address") user_address: RequestBody? = null,
//        @Field("user_password") user_password: RequestBody? = null,
//        @Part user_profile_pic: MultipartBody.Part? = null,
//        @Field("user_lat") user_lat: Double? = null,
//        @Field("user_long") user_long: Double? = null,
//        @Field user_signature: MultipartBody.Part? = null,
//        @Part("is_accept_pets") is_accept_pets: Int? = null,
//        @Part("is_crypto") is_crypto: Int? = null,
//        @Part("no_user_profile_pic") no_user_profile_pic: Boolean? = true,

    @POST("edit_signup")
    fun signUpEdit(
        @Body params: RequestBody,
    ): Single<SigUpResponse>

    @FormUrlEncoded
    @POST("change_password")
    fun changePassword(
        @Field("user_password") user_password: String,
        @Field("new_pass") new_password: String
    ): Single<ResponsePassword>

    @FormUrlEncoded
    @POST("forgot_password")
    fun forgotPassword(@Field("user_email_id") user_email_id: String): Single<ResponsePassword>

    @FormUrlEncoded
    @POST("reset_password")
    fun resetPassword(
        @Field("user_email_id") user_email_id: String?,
        @Field("temp_pass") temp_pass: String,
        @Field("new_pass") new_pass: String
    ): Single<ResponsePassword>


    @POST("add_vehicle_details")
    fun addVehicleDetails(
        @Body body: RequestBody
    ): Single<ResponsePassword>

    @Multipart
    @POST("add_emergency_contact")
    fun addEmergencyContact(
        @Part("contact_name") contact_name: RequestBody?,
        @Part("contact_phone_number") contact_phone_number: RequestBody?,
        @Part contact_profile_pic: MultipartBody.Part?,
        @Part("no_contact_profile_pic") no_contact_profile_pic: RequestBody?,
    ): Single<ResponsePassword>

    @POST("list_emergency_contact")
    fun listEmergencyContact(): Single<EmergencyContact>

    @FormUrlEncoded
    @POST("delete_emergency_contact")
    fun deleteEmergencyContact(
        @Field("emergency_contact_id") emergency_contact_id: Int
    ): Single<ResponsePassword>


    @FormUrlEncoded
    @POST("driver_profile")
    fun getDriverProfile(@Field("user_id") userId: Int?): Single<ProfileEntity>

    @POST("edit_driver_option")
    fun getEditDriverOption(
        @Body params: RequestBody?,
    ): Single<EditDriverOption>

    @Multipart
    @POST("edit_driver_option")
    fun EditDriverOption(
        @Part("driver_option_id") driver_option_id: Int,
        @Part("driver_option_value") driver_option_value: RequestBody? = null,
    ): Single<EditDriverOption>


    //    @Multipart
//
//    @Part("from_address") from_address: RequestBody?,
//    @Part("from_city") from_city: RequestBody?,
//    @Part("from_country") from_country: RequestBody?,
//    @Part("from_lat") from_lat: Double?,
//    @Part("from_long") from_long: Double?,
//    @Part("to_address") to_address: RequestBody?,
//    @Part("to_city") to_city: RequestBody?,
//    @Part("to_country") to_country: RequestBody?,
//    @Part("to_lat") to_lat: Double?,
//    @Part("to_long") to_long: Double?,
//    @Part("fare_cost_by_user") fare_cost_by_user: Int?,
//    @Part("payment_method_type") payment_method_type: Int?,
//    @Part("fare_bidding_time") fare_bidding_time: RequestBody?,
//    @Part("vehicle_type_id") vehicle_type_id: Int?,
//    @Part("fare_comments") fare_comments: RequestBody?,
//    @Part("fare_image_type") fare_image_type: Int?,
//    @Part fare_image_url: MultipartBody.Part?,
//    @Part("no_fare_image_url") no_fare_image_url: Int? = 0
    @POST("add_fare")
    fun addFare(
        @Body addFair: RequestBody
    ): Single<AddFairResponse>

    //
//    @Multipart
//    @Part("fare_image_type") fare_image_type: Int?,
//    @Part("fare_id") fare_id: Int?,
//    @Part("fare_image_url") fare_image_url: RequestBody?,
//    @Part("no_fare_image_url") no_fare_image_url: Int? = 1
    @POST("add_selfie_signature")
    fun addSelfieSignature(
        @Body selfieParams: RequestBody
    ): Single<ResponsePassword>


    @FormUrlEncoded
    @POST("fare_list")
    fun getFairList(@Field("fare_id") fare_id: Int?): Single<FairList>


    @FormUrlEncoded
    @POST("send_offer_to_user")
    fun sendOfferUser(
        @Field("driver_bid_price") driver_bid_price: Int?,
        @Field("fare_id") fare_id: Int?
    ): Single<ResponsePassword>

    @FormUrlEncoded
    @POST("add_review_to_driver")
    fun addReviewToDriver(
        @Field("review_to") review_to: Int?,
        @Field("fare_id") fare_id: Int?,
        @Field("no_of_star") no_of_star: Float?
    ): Single<ResponsePassword>

    @FormUrlEncoded
    @POST("driver_request_list")
    fun getDriverRequestList(
        @Field("fare_id") fare_id: Int?,
        @Field("user_id") user_id: Int?
    ): Single<DriverRequestListModel>

    @POST("booking_history_driver")
    fun getBookingHistoryDriver(): Single<BookingHistoryEntity>

    @POST("booking_history_user")
    fun getBookingHistoryUser(): Single<BookingHistoryEntity>

    @FormUrlEncoded
    @POST("fare_booking")
    fun fareBooking(
        @Field("fare_id") fare_id: Int?,
        @Field("fare_booking_amount") fare_booking_amount: Int?,
        @Field("fare_booking_transaction_id") fare_booking_transaction_id: Int?,
        @Field("user_id") user_id: Int?,
        @Field("payment_method_id") payment_method_id: Int?,
        @Field("fare_driver_bid_id") fare_driver_bid_id: Int?
    ): Single<ResponsePassword>

    @FormUrlEncoded
    @POST("log_out")
    fun logout(@Field("device_id") deviceId: String?): Single<Logout>

    @FormUrlEncoded
    @POST("cancle_ride")
    fun CancelRide(
        @Field("fare_booking_id") fare_booking_id: String?,
        @Field("booking_text") booking_text: String?
    ): Single<ResponsePassword>

    @FormUrlEncoded
    @POST("update_request_status")
    fun updateRequestStatus(
        @Field("fare_driver_bid_id") fare_driver_bid_id: Int,
        @Field("driver_bid_status") driver_bid_status: Int,
        @Field("fare_id") fair_id: Int
    ): Single<ResponsePassword>

    @FormUrlEncoded
    @POST("list_fare_users")
    fun listFairUser(
        @Field("new_distance") new_distance: Int? = null,
        @Field("user_lat") user_lat: Double,
        @Field("user_long") user_long: Double
    ): Single<ListFairUser>

    @FormUrlEncoded
    @POST("add_bank_details")
    fun addBankDetails(
        @Field("user_bank_account_no") user_bank_account_no: String?,
        @Field("user_bank_account_name") user_bank_account_name: String?,
        @Field("user_bank_ifsc_code") user_bank_ifsc_code: String?,
        @Field("user_bank_name") user_bank_name: String?,
        @Field("user_bank_branch") user_bank_branch: String?
    ): Single<ResponsePassword>

    @FormUrlEncoded
    @POST("pickup_done_by_driver")
    fun pikUpDoneByDriver(
        @Field("fare_id") fare_id: Int?,
        @Field("is_confirmed_by") is_confirmed_by: Int // 1=otp way,2=location way //1 apso to pickup done thai ne otp send thase and 2 apso to only pickup done thase
    ): Single<ResponsePassword>


    @FormUrlEncoded
    @POST("verify_pickup_otp")
    fun verifyPickupOtp(
        @Field("fare_id") fair_id: Int?,
        @Field("pickup_otp") pickup_otp: String? = null,
        @Field("is_confirmed_by") is_confirmed_by: Int? //1 apo to pickup_otp params apjo and 2 apo to nhi apta
    ): Single<ResponsePassword>

    @FormUrlEncoded
    @POST("pickup_done_by_user")
    fun pikUpDoneByUser(
        @Field("fare_id") fare_id: Int?,
        @Field("is_confirmed") is_confirmed: Int //1=pickup done,2= not done
    ): Single<ResponsePassword>


    @POST("fare_completed_by_driver")
    fun fairCompletedByDriver(@Query("fare_id") fare_id: Int?): Single<ResponsePassword>

    @FormUrlEncoded
    @POST("fare_completed_by_user")
    fun fairCompletedByUser(
        @Field("fare_id") fare_id: Int?,
        @Field("is_confirmed") is_confirmed: Int? ///1=fare completed,2=not completed
    ): Single<ResponsePassword>


    @POST("driver_ongoing_fare_details")
    fun driverOngoingFareDetails(): Single<DriverOngoingFairDetails>

    fun acceptRequestByDriver(@Query("fare_id") fare_id: Int?): Single<ResponsePassword>

}