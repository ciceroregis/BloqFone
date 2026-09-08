package br.com.bloqfone.data

import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat

class ContactsRepository(private val context: Context) {

    fun doesNumberExistInContacts(phoneNumber: String): Boolean {
        // Security check: Verify if the user granted permission to read contacts
        if(ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.READ_CONTACTS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return false
        }
        // Prepare the query to check if the number exists in contacts
        val uri = Uri.withAppendedPath(
            android.provider.ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        //We only need the display name to check if the contact exists
        val projection = arrayOf(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME)
        context.contentResolver.query(
            uri, projection, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            if (cursor.moveToFirst()){ //The number was found in the contacts
                return true
            }
        }
        return false // The number was not found in the contacts
    }

}