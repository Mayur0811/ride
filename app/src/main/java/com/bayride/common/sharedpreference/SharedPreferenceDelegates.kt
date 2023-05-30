package com.bayride.common.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.bayride.StorageConfig
import com.bayride.common.utils.Constants
import com.bayride.data.models.dto.Driver
import com.bayride.data.models.dto.PassengerSignup
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class StringPreferenceDelegate(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: String = ""
) : ReadWriteProperty<Any, String> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}

class StringSetPreferenceDelegate(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: Set<String> = setOf()
) : ReadWriteProperty<Any, Set<String>> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Set<String>) {
        sharedPreferences.edit {
            putStringSet(key, value)
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): Set<String> {
        return sharedPreferences.getStringSet(key, defaultValue) ?: defaultValue
    }
}

class IntPreferenceDelegate(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: Int = 0,
) : ReadWriteProperty<Any, Int> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        sharedPreferences.edit {
            putInt(key, value)
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }
}

class LongPreferenceDelegate(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: Long = 0,
) : ReadWriteProperty<Any, Long> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        sharedPreferences.edit {
            putLong(key, value)
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }
}

class FloatPreferenceDelegate(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: Float = 0f,
) : ReadWriteProperty<Any, Float> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Float) {
        sharedPreferences.edit {
            putFloat(key, value)
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }
}

class BooleanPreferenceDelegate(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: Boolean = false,
) : ReadWriteProperty<Any, Boolean> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        sharedPreferences.edit {
            putBoolean(key, value)
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}

class KeyExistPreferenceDelegate(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
) : ReadOnlyProperty<Any, Boolean> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return sharedPreferences.contains(key)
    }
}

fun SharedPreferences.removeData(vararg keys: String) {
    edit {
        keys.forEach { key -> remove(key) }
    }
}


fun getEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences? {
    val sharedPreferences =
        context.getSharedPreferences(StorageConfig.SECURE_SHARED_PREFERENCES_FILE_NAME, 0)
    return sharedPreferences


//    val masterKey: MasterKey =
//        MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
//            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//            .build()
//
//    return EncryptedSharedPreferences.create(
//        context,
//        StorageConfig.SECURE_SHARED_PREFERENCES_FILE_NAME,
//        masterKey, // masterKey created above
//        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//    )
}

fun saveObjectToSharedPreference(
    context: Context,
    preferenceFileName: String?,
    serializedObjectKey: String?,
    signupModel: PassengerSignup,
    flag: Boolean,
    signatureClear: Boolean
) {
    if (flag) {
        getSavedObjectFromPreference(
            context,
            Constants.BAYRIDE_PASSENGER_MODEL,
            Constants.PASSENGER,
            PassengerSignup::class.java
        )?.apply {
            if (signupModel.photo == null) {
                signupModel.photo = photo
            }
            if (signupModel.name == null) {
                signupModel.name = name
            }
            if (signupModel.username == null) {
                signupModel.username = username
            }
            if (signupModel.email == null) {
                signupModel.email = email
            }
            if (signupModel.phoneNumber == null) {
                signupModel.phoneNumber = phoneNumber
            }
            if (signupModel.address == null) {
                signupModel.address = address
            }
            if (signupModel.createPassword == null) {
                signupModel.createPassword = createPassword
            }

            if (signatureClear) {
                signupModel.signature = null
            } else {
                if (signupModel.signature == null) {
                    signupModel.signature = signature
                }
            }
            if (signupModel.enterPaymentDetails == null) {
                signupModel.enterPaymentDetails = enterPaymentDetails
            }
        }
    }
    val sharedPreferences = context.getSharedPreferences(preferenceFileName, 0)
    val sharedPreferencesEditor = sharedPreferences.edit()
    val gson = Gson()
    val serializedObject = gson.toJson(signupModel)
    sharedPreferencesEditor.putString(serializedObjectKey, serializedObject)
    sharedPreferencesEditor.apply()
}

fun saveModelObjectToSharedPreference(
    context: Context,
    preferenceFileName: String?,
    serializedObjectKey: String?,
    objectModel: Any?
) {
    val sharedPreferences = context.getSharedPreferences(preferenceFileName, 0)
    val sharedPreferencesEditor = sharedPreferences.edit()
    val gson = Gson()
    val serializedObject = gson.toJson(objectModel)
    sharedPreferencesEditor.putString(serializedObjectKey, serializedObject)
    sharedPreferencesEditor.apply()
}

fun saveDriverObjectToSharedPreference(
    context: Context,
    preferenceFileName: String?,
    serializedObjectKey: String?,
    driver: Driver,
    flag: Boolean
) {
    if (flag) {
        getSavedObjectFromPreference(
            context,
            Constants.BAYRIDE_DRIVER_MODEL,
            Constants.DRIVER,
            Driver::class.java
        )?.apply {
            if (driver.contactInformation == null) {
                driver.contactInformation = contactInformation
            }
            if (driver.vehicle_photo == null) {
                driver.vehicle_photo = vehicle_photo
            }
            if (driver.vehicleDetails == null) {
                driver.vehicleDetails = vehicleDetails
            }
            if (driver.uploadDriverLicense == null) {
                driver.uploadDriverLicense = uploadDriverLicense
            }
            if (driver.insuranceInformation == null) {
                driver.insuranceInformation = insuranceInformation
            }
            if (driver.uploadInsuranceCard == null) {
                driver.uploadInsuranceCard = uploadInsuranceCard
            }
            if (driver.accepts_crypto == null) {
                driver.accepts_crypto = accepts_crypto
            }
            if (driver.accepts_pets == null) {
                driver.accepts_pets = accepts_pets
            }
            if (driver.bankDetails == null) {
                driver.bankDetails = bankDetails
            }
        }
    }
    val sharedPreferences = context.getSharedPreferences(preferenceFileName, 0)
    val sharedPreferencesEditor = sharedPreferences.edit()
    val gson = Gson()
    val serializedObject = gson.toJson(driver)
    sharedPreferencesEditor.putString(serializedObjectKey, serializedObject)
    sharedPreferencesEditor.apply()

}


fun <GenericClass> getSavedObjectFromPreference(
    context: Context,
    preferenceFileName: String?,
    preferenceKey: String?,
    classType: Class<GenericClass>?
): GenericClass? {
    val sharedPreferences = context.getSharedPreferences(preferenceFileName, 0)
    if (sharedPreferences.contains(preferenceKey)) {
        val gson = Gson()
        return gson.fromJson(sharedPreferences.getString(preferenceKey, ""), classType)
    }
    return null
}
//fun set(@ApplicationContext context: Context,passengerSignup: PassengerSignup) {
//    val appSharedPrefs = PreferenceManager
//        .getDefaultSharedPreferences(context)
//    val prefsEditor = appSharedPrefs.edit()
//    val setgson = Gson()
//    val setjson = setgson.toJson(passengerSignup)
//    prefsEditor.putString(Constants.PASSENGER, setjson)
//    prefsEditor.commit()
//}
//
//fun getPassenger(context: Context): PassengerSignup {
//    val appSharedPrefs = PreferenceManager
//        .getDefaultSharedPreferences(context)
//    val gson = Gson()
//    val json = appSharedPrefs.getString(Constants.PASSENGER, "")
//    val obj = gson.fromJson(json, PassengerSignup::class.java)
//    return obj
//}
