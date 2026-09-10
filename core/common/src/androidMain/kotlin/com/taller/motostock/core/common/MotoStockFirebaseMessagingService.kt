package com.taller.motostock.core.common

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MotoStockFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_CITAS    = "channel_citas"
        const val CHANNEL_STOCK    = "channel_stock"

        /**
         * Guarda (o actualiza) el token FCM del usuario actualmente autenticado
         * en Firestore: /usuarios/{uid}/fcmToken
         */
        fun guardarTokenSiHayUsuario(token: String) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .update("fcmToken", token)
                .addOnFailureListener {
                    // Si el doc no existe todavía, lo crea con set+merge
                    FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .document(uid)
                        .set(mapOf("fcmToken" to token), com.google.firebase.firestore.SetOptions.merge())
                }
        }

        /** Crea los canales de notificación (Android 8+). Llamar desde Application.onCreate() */
        fun crearCanales(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.createNotificationChannel(
                    NotificationChannel(CHANNEL_CITAS, "Citas", NotificationManager.IMPORTANCE_HIGH).apply {
                        description = "Notificaciones de citas y reservas"
                    }
                )
                nm.createNotificationChannel(
                    NotificationChannel(CHANNEL_STOCK, "Inventario", NotificationManager.IMPORTANCE_DEFAULT).apply {
                        description = "Alertas de stock bajo en inventario"
                    }
                )
            }
        }

        /** Muestra una notificación local directamente (sin pasar por FCM). */
        fun mostrarNotificacionLocal(
            context: Context,
            titulo: String,
            cuerpo: String,
            channelId: String = CHANNEL_CITAS,
            notifId: Int = System.currentTimeMillis().toInt()
        ) {
            val intent = (context.packageManager
                .getLaunchIntentForPackage(context.packageName) ?: Intent())
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
            val pi = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notif = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setStyle(NotificationCompat.BigTextStyle().bigText(cuerpo))
                .setPriority(
                    if (channelId == CHANNEL_CITAS) NotificationCompat.PRIORITY_HIGH
                    else NotificationCompat.PRIORITY_DEFAULT
                )
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build()

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(context).notify(notifId, notif)
            }
        }
    }

    /** Se llama cuando el token FCM es renovado por el sistema. */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        guardarTokenSiHayUsuario(token)
    }

    /** Se llama cuando llega un mensaje FCM mientras la app está en primer plano. */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val titulo = message.notification?.title ?: message.data["title"] ?: return
        val cuerpo = message.notification?.body ?: message.data["body"] ?: return
        val canal  = message.data["channel"] ?: CHANNEL_CITAS

        mostrarNotificacionLocal(
            context   = applicationContext,
            titulo    = titulo,
            cuerpo    = cuerpo,
            channelId = canal
        )
    }
}
