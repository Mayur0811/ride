package com.bayride.data.datasources.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.bayride.common.utils.FileUtils
import com.bayride.common.utils.FileUtils.getBitmap
import com.bayride.data.datasources.remote.api.ApiService
import com.bayride.data.datasources.remote.entities.BookingHistoryEntity
import com.bayride.data.datasources.remote.entities.ProfileEntity
import com.bayride.data.datasources.remote.entities.ResponsePassword
import com.bayride.data.models.dto.DriverOngoingFairDetails
import com.bayride.data.models.dto.EditDriverOption
import com.bayride.data.models.dto.FairList
import com.bayride.data.models.dto.ListFairUser
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject


class DriverRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) : IDriverRemoteDataSource {

    override fun addVehicleDetails(
        vehicle_number: String?,
        vehicle_license_number:String?,
        vehicle_year: String?,
        vehicle_model: String?,
        vehicle_colour: String?,
        vehicle_type_id: Int?,
        vehicle_image: File?,
        vehicle_brand: String?
    ): Single<ResponsePassword> {

        val builder: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        if (vehicle_number != null) {
            builder.addFormDataPart("vehicle_number", vehicle_number.toString())
        }
        if (vehicle_license_number != null) {
            builder.addFormDataPart("vehicle_license_number", vehicle_license_number.toString())
        }
        if (vehicle_year != null) {
            builder.addFormDataPart("vehicle_year", vehicle_year.toString())
        }
        if (vehicle_model != null) {
            builder.addFormDataPart("vehicle_model", vehicle_model.toString())
        }
        if (vehicle_colour != null) {
            builder.addFormDataPart("vehicle_colour", vehicle_colour.toString())
        }
        if (vehicle_type_id != null) {
            builder.addFormDataPart("vehicle_type_id", vehicle_type_id.toString())
        }
        if (vehicle_brand != null) {
            builder.addFormDataPart("vehicle_brand", vehicle_brand.toString())
        }
        if (vehicle_image != null) {
            builder.addFormDataPart(
                "vehicle_image",
                vehicle_image.name,
                RequestBody.create(
                    MultipartBody.FORM,
                    FileUtils.getBitmap(vehicle_image).toByteArray()
                )
            )
        }
        return apiService.addVehicleDetails(
            builder.build()
        )
    }

    override fun getDriverProfile(userId: Int?): Single<ProfileEntity> {
        return apiService.getDriverProfile(userId)
    }

    override fun getEditDriverOption(
        driver_option_id: Int,
        driver_option_value: String?,
        driver_option_value_file: File?
    ): Single<EditDriverOption> {
//        val requestFile: RequestBody? = driver_option_value_file?.path?.let {
//            RequestBody.create(
//                "multipart/form-data".toMediaTypeOrNull(),
//                it
//            )
//        }
//        val drivingLicence: MultipartBody.Part? =
//            requestFile?.let {
//                MultipartBody.Part.createFormData(
//                    "driver_option_value",
//                    driver_option_value_file.name,
//                    it
//                )
//            }

        val body: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        if (driver_option_value_file != null) {
            body.addFormDataPart(
                "driver_option_value",
                driver_option_value_file?.getName(),
                RequestBody.create(
                    MultipartBody.FORM,
                    getBitmap(driver_option_value_file).toByteArray()
                )
            ).addFormDataPart("driver_option_id", driver_option_id.toString())
        }

        return if (driver_option_value_file == null) {
            apiService.EditDriverOption(driver_option_id, driver_option_value?.toRequestBody())
        } else {
            apiService.getEditDriverOption(
                body.build()
            )
        }
    }

    override fun sendOfferUser(driver_bid_price: Int?, fare_id: Int?): Single<ResponsePassword> {
        return apiService.sendOfferUser(driver_bid_price, fare_id)
    }

    override fun getBookingHistoryDriver(): Single<BookingHistoryEntity> {
        return apiService.getBookingHistoryDriver()
    }

    override fun fareBooking(
        fare_id: Int?,
        fare_booking_amount: Int?,
        fare_booking_transaction_id: Int?,
        user_id: Int?,
        payment_method_id: Int?,
        fair_driver_bid_id: Int?
    ): Single<ResponsePassword> {
        return apiService.fareBooking(
            fare_id,
            fare_booking_amount,
            fare_booking_transaction_id,
            user_id,
            payment_method_id,
            fair_driver_bid_id
        )
    }

    override fun listFairUser(
        new_distance: Int?,
        user_lat: Double,
        user_long: Double
    ): Single<ListFairUser> {
        return apiService.listFairUser(new_distance, user_lat, user_long)
    }

    override fun addBankDetails(
        user_bank_account_no: String?,
        user_bank_account_name: String?,
        user_bank_ifsc_code: String?,
        user_bank_name: String?,
        user_bank_branch: String?
    ): Single<ResponsePassword> {
        return apiService.addBankDetails(
            user_bank_account_no,
            user_bank_account_name,
            user_bank_ifsc_code,
            user_bank_name,
            user_bank_branch
        )
    }

    override fun pikUpDoneByDriver(fare_id: Int?, is_confirmed_by: Int): Single<ResponsePassword> {
        return apiService.pikUpDoneByDriver(fare_id, is_confirmed_by)
    }

    override fun verifyPickupOtp(
        fair_id: Int?,
        pickup_otp: String?,
        is_confirmed_by: Int?
    ): Single<ResponsePassword> {
        return apiService.verifyPickupOtp(fair_id, pickup_otp, is_confirmed_by)
    }

    override fun fairCompletedByDriver(fare_id: Int?): Single<ResponsePassword> {
        return apiService.fairCompletedByDriver(fare_id)
    }

    override fun driverOngoingFareDetails(): Single<DriverOngoingFairDetails> {
        return apiService.driverOngoingFareDetails()
    }

    override fun acceptRequestByDriver(fair_id: Int?): Single<ResponsePassword> {
        return apiService.acceptRequestByDriver(fair_id)
    }

    override fun getFairList(fare_id: Int?): Single<FairList> {
        return apiService.getFairList(fare_id)
    }

}