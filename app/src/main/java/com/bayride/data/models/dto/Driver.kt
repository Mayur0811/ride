package com.bayride.data.models.dto

import kotlinx.serialization.Serializable

data class Driver(
    var contactInformation: ContactInformation? = null,
    var vehicle_photo: String? = null,
    var vehicleDetails: VehicleDetails? = null,
    var uploadDriverLicense: String? = null,
    var insuranceInformation: InsuranceInformation? = null,
    var uploadInsuranceCard: String? = null,
    var accepts_crypto: Int? = null,
    var accepts_pets: Int? = null,
    var bankDetails: BankDetails? = null
)


@Serializable
data class ContactInformation(
    val profile_pic: String?,
    val username: String?,
    val name: String? = null,
    val email: String,
    val phone_number: String,
    val country_Code: String,
    val address: String,
    val password: String,

    )

@Serializable
data class VehicleDetails(
    val vehicle_number: String?,
    val vehicle_license_number:String?,
    val brand: String?,
    val model: String?,
    val year: String,
    val color: String,
    val type: Int?
)

@Serializable
data class InsuranceInformation(
    val policy_holder_name: String?,
    val policy_number: String,
    val phone_number: String,
    val country_Code: String
)

@Serializable
data class BankDetails(
    val account_name: String?,
    val account_number: String?,
    val ifsc_code: String?,
    val bankName: String?,
    val branch: String?
)

