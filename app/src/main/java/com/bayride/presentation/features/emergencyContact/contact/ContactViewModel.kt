package com.bayride.presentation.features.emergencyContact.contact

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import com.bayride.common.reactive.applyIO
import com.bayride.common.utils.Constants
import com.bayride.data.datasources.remote.entities.BadRequest
import com.bayride.data.models.dto.Contact
import com.bayride.data.repositories.authentication.AuthenticationRepository
import com.bayride.presentation.base.BaseViewModel
import com.bayride.presentation.features.signup.SignUpError
import com.bayride.presentation.features.signup.SignUpFailed
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(val authenticationRepository: AuthenticationRepository) :
    BaseViewModel<ContactViewState, ContactEvent>() {
    override fun initState(): ContactViewState {
        return ContactViewState()
    }


    fun addEmergencyContact(
        contact_name: String?,
        contact_phone_number: String?,
        contact_profile_pic: File?,
        no_contact_profile_pic: String?,
    ) {
        dispatchState(currentState.copy(loading = true))
        authenticationRepository.addEmergencyContact(
            contact_name,
            contact_phone_number,
            contact_profile_pic,
            no_contact_profile_pic
        ).applyIO()
            .doFinally {
                dispatchState(currentState.copy(loading = false))
            }
            .subscribe({
                dispatchEvent(ContactGetSuccessFully)

            }, { error ->
                val errorMessage = BadRequest.parse(error)?.detail
                val exception = errorMessage
                    ?.let { RuntimeException(errorMessage) }
                    ?: run { error }
                if (error is HttpException) {
                    val code = error.code()
                    dispatchEvent(ContactErrorEvent(code, error.message()))
                } else {
                    dispatchEvent(ContactFailedEvent(exception))
                }
            })
    }

    @SuppressLint("Range")
    fun getContacts(context: Context) {
        var list: MutableList<Contact> = ArrayList<Contact>()

        val contentResolver: ContentResolver = context.contentResolver
        var uri: Uri? = null
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ContactsContract.CommonDataKinds.Contactables.CONTENT_URI
        } else {
            ContactsContract.Data.CONTENT_URI
        }
        val cursor =
            contentResolver.query(
                uri,
                arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                "length(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ") >= 8 AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " NOT LIKE '*%' AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0",
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )
        if (cursor!!.count > 0) {
            try {
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

                    val person = ContentUris.withAppendedId(
                        ContactsContract.Contacts.CONTENT_URI,
                        id.toLong()
                    )
                    val pURI = Uri.withAppendedPath(
                        person,
                        ContactsContract.Contacts.Photo.CONTENT_DIRECTORY
                    )

                    val contactName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val contactNo =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val contact = Contact(
                        contactName,
                        contactNo,
                        Constants.regExp.replace(contactNo.replace(" ", ""), ""),
                        null,
                        pURI.path,
                        false
                    )
                    list.add(contact)

                }
                cursor.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        list = list.distinctBy {
            it.filteredNumber
        } as MutableList<Contact>
        dispatchState(currentState.copy(loading = false))
        dispatchState(currentState.copy(contactList = list))
    }

}