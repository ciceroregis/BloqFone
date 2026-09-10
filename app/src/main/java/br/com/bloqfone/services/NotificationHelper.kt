package br.com.bloqfone.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import br.com.bloqfone.MainActivity
import br.com.bloqfone.R
import br.com.bloqfone.data.formatPhoneNumberForDisplay

object NotificationHelper {

    private const val TAG = "BloqFone:Notification"
    const val CHANNEL_ID = "bloqfone_blocked_calls_channel"
    private const val CHANNEL_NAME = "Chamadas Bloqueadas"
    private const val CHANNEL_DESC = "Notificações sobre chamadas interceptadas e bloqueadas pelo BloqFone"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                if (notificationManager != null) {
                    val existing = notificationManager.getNotificationChannel(CHANNEL_ID)
                    if (existing == null) {
                        val channel = NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT
                        ).apply {
                            description = CHANNEL_DESC
                            enableVibration(true)
                            setShowBadge(true)
                        }
                        notificationManager.createNotificationChannel(channel)
                        Log.i(TAG, "[SUCESSO] Canal de notificação '$CHANNEL_ID' criado com sucesso.")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "[ERRO] Falha ao criar canal de notificação.", e)
            }
        }
    }

    fun notifyBlockedCall(context: Context, rawNumber: String?, reason: String) {
        try {
            // Em Android 13+ (API 33+), verifica se a permissão foi concedida antes de disparar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionStatus = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                )
                if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "[AVISO] Permissão POST_NOTIFICATIONS não concedida. Notificação não pôde ser enviada.")
                    return
                }
            }

            createNotificationChannel(context)

            val display = when {
                rawNumber.isNullOrBlank() -> "Número Privado / Oculto"
                else -> formatPhoneNumberForDisplay(rawNumber).ifEmpty { rawNumber }
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("EXTRA_NAVIGATE_TO", "report")
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notificationTitle = "Chamada Bloqueada"
            val notificationText = "$display bloqueado ($reason)"
            val detailedText = "A chamada de $display foi bloqueada automaticamente pelo BloqFone.\nMotivo: $reason."

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setStyle(NotificationCompat.BigTextStyle().bigText(detailedText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            val notificationId = (System.currentTimeMillis() % 100000).toInt()
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            Log.i(TAG, "[SUCESSO] Notificação de chamada bloqueada disparada (ID=$notificationId, número=$display, motivo='$reason').")
        } catch (e: Exception) {
            Log.e(TAG, "[ERRO] Falha ao disparar notificação de chamada bloqueada.", e)
        }
    }
}
