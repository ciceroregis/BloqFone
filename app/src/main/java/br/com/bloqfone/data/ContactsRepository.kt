package br.com.bloqfone.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat

class ContactsRepository(private val context: Context) {

    companion object {
        private const val TAG = "BloqFone:ContactsRepo"
    }

    fun doesNumberExistInContacts(phoneNumber: String): Boolean {
        if (phoneNumber.isBlank()) {
            Log.d(TAG, "[RASTREAMENTO] Verificação de contato ignorada: número vazio.")
            return false
        }
        val masked = maskPhoneNumberForLog(phoneNumber)
        if (
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_CONTACTS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "[AVISO] Permissão READ_CONTACTS não concedida. Não foi possível verificar se $masked está nos contatos.")
            return false
        }
        return try {
            val uri = Uri.withAppendedPath(
                android.provider.ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            val projection = arrayOf(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME)
            val exists = context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                cursor.moveToFirst()
            } ?: false

            if (exists) {
                Log.d(TAG, "[SUCESSO] Consulta de contatos para $masked finalizada com sucesso (encontrado=true via PhoneLookup).")
                return true
            }

            // Fallback: busca pelos últimos 8 dígitos na tabela de telefones
            val digits = phoneNumber.filter { it.isDigit() }
            if (digits.length >= 8) {
                val last8 = digits.takeLast(8)
                val phoneUri = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                val selection = "${android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?"
                val selectionArgs = arrayOf("%$last8%")
                val foundByDigits = context.contentResolver.query(
                    phoneUri,
                    arrayOf(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER),
                    selection,
                    selectionArgs,
                    null
                )?.use { cursor ->
                    cursor.moveToFirst()
                } ?: false

                if (foundByDigits) {
                    Log.d(TAG, "[SUCESSO] Consulta de contatos para $masked finalizada com sucesso (encontrado=true via dígitos).")
                    return true
                }
            }

            Log.d(TAG, "[SUCESSO] Consulta de contatos para $masked finalizada com sucesso (encontrado=false).")
            false
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Erro ao consultar provedor de contatos para o número $masked.", e)
            false
        }
    }
}