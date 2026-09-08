package br.com.bloqfone.data

import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat

class ContactsRepository(private val context: Context) {

    fun doesNumberExistInContacts(phoneNumber: String): Boolean {
        if (phoneNumber.isBlank()) return false
        if (
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_CONTACTS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        val uri = Uri.withAppendedPath(
            android.provider.ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME)
        context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            return cursor.moveToFirst()
        }
        return false
    }
}