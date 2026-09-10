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
            Log.d(TAG, "[SUCESSO] Consulta de contatos para $masked finalizada com sucesso (encontrado=$exists).")
            exists
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Erro ao consultar provedor de contatos para o número $masked.", e)
            false
        }
    }
}