package br.com.bloqfone.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class BlockedCallRecord(
    val id: String,
    val rawNumber: String?,
    val formattedDisplayNumber: String,
    val timestamp: Long,
    val reason: String,
    val wasAutoRejected: Boolean
) {
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}

class BlockedCallsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "BloqFone:BlockedCallsRepo"
        private const val PREFS_NAME = "bloqfone_blocked_calls"
        private const val KEY_BLOCKED_CALLS = "blocked_calls_list_json"
        private const val MAX_RECORDS = 500
    }

    fun recordBlockedCall(rawNumber: String?, reason: String, autoReject: Boolean): BlockedCallRecord {
        val display = when {
            rawNumber.isNullOrBlank() -> "Número Privado / Oculto"
            else -> formatPhoneNumberForDisplay(rawNumber).ifEmpty { rawNumber }
        }

        val record = BlockedCallRecord(
            id = UUID.randomUUID().toString(),
            rawNumber = rawNumber,
            formattedDisplayNumber = display,
            timestamp = System.currentTimeMillis(),
            reason = reason,
            wasAutoRejected = autoReject
        )

        try {
            val existing = getBlockedCalls().toMutableList()
            existing.add(0, record)
            if (existing.size > MAX_RECORDS) {
                existing.subList(MAX_RECORDS, existing.size).clear()
            }
            saveBlockedCalls(existing)
            Log.i(
                TAG,
                "[SUCESSO] Chamada bloqueada registrada no relatório: número=${maskPhoneNumberForLog(rawNumber)}, motivo='$reason', autoReject=$autoReject"
            )
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Falha ao persistir registro de chamada bloqueada no relatório.", e)
        }

        return record
    }

    fun getBlockedCalls(): List<BlockedCallRecord> {
        val jsonString = prefs.getString(KEY_BLOCKED_CALLS, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = ArrayList<BlockedCallRecord>(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val rawNumber = if (obj.has("rawNumber") && !obj.isNull("rawNumber")) obj.getString("rawNumber") else null
                list.add(
                    BlockedCallRecord(
                        id = obj.getString("id"),
                        rawNumber = rawNumber,
                        formattedDisplayNumber = obj.optString(
                            "formattedDisplayNumber",
                            if (rawNumber.isNullOrBlank()) "Número Privado / Oculto" else formatPhoneNumberForDisplay(rawNumber)
                        ),
                        timestamp = obj.getLong("timestamp"),
                        reason = obj.getString("reason"),
                        wasAutoRejected = obj.optBoolean("wasAutoRejected", true)
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Falha ao decodificar relatório de chamadas bloqueadas.", e)
            emptyList()
        }
    }

    fun clearAll(): Boolean {
        return try {
            prefs.edit { remove(KEY_BLOCKED_CALLS) }
            Log.i(TAG, "[SUCESSO] Todos os registros do relatório de chamadas foram removidos.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Falha ao limpar relatório de chamadas bloqueadas.", e)
            false
        }
    }

    fun deleteRecord(id: String): Boolean {
        return try {
            val current = getBlockedCalls().toMutableList()
            val removed = current.removeAll { it.id == id }
            if (removed) {
                saveBlockedCalls(current)
                Log.i(TAG, "[SUCESSO] Registro de chamada $id excluído do relatório.")
            }
            removed
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Falha ao excluir registro do relatório.", e)
            false
        }
    }

    fun getCount(): Int = getBlockedCalls().size

    private fun saveBlockedCalls(list: List<BlockedCallRecord>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("rawNumber", item.rawNumber)
                put("formattedDisplayNumber", item.formattedDisplayNumber)
                put("timestamp", item.timestamp)
                put("reason", item.reason)
                put("wasAutoRejected", item.wasAutoRejected)
            }
            jsonArray.put(obj)
        }
        prefs.edit { putString(KEY_BLOCKED_CALLS, jsonArray.toString()) }
    }
}
