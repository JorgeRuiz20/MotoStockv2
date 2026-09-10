package com.taller.motostock.core.common

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.Repuesto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper centralizado para enviar notificaciones push.
 *
 * Estrategia:
 *  - Trabajador → Cliente: obtiene el fcmToken del cliente desde Firestore
 *    (colección "usuarios/{uid}/fcmToken") y escribe un documento en
 *    "fcm_queue/{docId}" para que una Cloud Function lo procese y llame a FCM API.
 *    Mientras tanto, si el cliente tiene la app abierta, el onMessageReceived
 *    del FirebaseMessagingService mostrará la notificación.
 *
 *  - Trabajador (stock bajo): muestra notificación LOCAL directamente, ya que
 *    el trabajador que descuenta el stock ya tiene la app en primer plano.
 *
 * NOTA: Para que las notificaciones lleguen cuando la app está en background
 * debes desplegar la Cloud Function incluida en la guía GUIA_NOTIFICACIONES.md.
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) {

    // ─────────────────────────────────────────────────────────────────────────
    // Notificaciones de CITAS → para el CLIENTE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Notifica al cliente que su cita fue ACEPTADA.
     * Mensaje: "Tu cita ha sido aceptada para las [hora]. ¡Por favor no llegues tarde!"
     */
    suspend fun notificarCitaAceptada(cita: Cita) {
        val hora = cita.horaDeseada.ifBlank { "la hora acordada" }
        val titulo = "✅ Cita aceptada"
        val cuerpo = "Tu cita ha sido aceptada para las $hora. " +
                     "¡Por favor no llegues tarde, te esperamos en el taller!"
        enviarNotificacionACliente(cita, titulo, cuerpo)
    }

    /**
     * Notifica al cliente que su cita fue RECHAZADA.
     */
    suspend fun notificarCitaRechazada(cita: Cita) {
        val titulo = "❌ Cita no disponible"
        val cuerpo = "Lo sentimos, hemos rechazado tu cita. " +
                     "Si quieres saber el motivo, entra a tu app y vuelve a solicitarla. ¡Te esperamos!"
        enviarNotificacionACliente(cita, titulo, cuerpo)
    }

    /**
     * Notifica al cliente que su cita fue CANCELADA (después de haber sido aceptada).
     */
    suspend fun notificarCitaCancelada(cita: Cita) {
        val titulo = "⚠️ Cita cancelada"
        val cuerpo = "Tu cita ha sido cancelada. " +
                     "Entra a la app para ver el motivo y reagendar cuando gustes. ¡Disculpa los inconvenientes!"
        enviarNotificacionACliente(cita, titulo, cuerpo)
    }

    /**
     * Notifica a todos los TRABAJADORES que llegó una nueva cita pendiente.
     * (Notificación local ya que los trabajadores tienen la app abierta;
     *  también encola en fcm_queue para background.)
     */
    suspend fun notificarNuevaCitaATrabajadores(cita: Cita) {
        val titulo = "🔔 Nueva cita recibida"
        val cuerpo = "Placa: ${cita.placa} | ${cita.propietario}\n" +
                     "Servicio: ${cita.tipoServicio} — Hora deseada: ${cita.horaDeseada}"

        // Muestra localmente (por si hay un trabajador con la app abierta)
        MotoStockFirebaseMessagingService.mostrarNotificacionLocal(
            context   = context,
            titulo    = titulo,
            cuerpo    = cuerpo,
            channelId = MotoStockFirebaseMessagingService.CHANNEL_CITAS,
            notifId   = ("nueva_cita_${cita.id}").hashCode()
        )

        // Encola en Firestore para que Cloud Function notifique a trabajadores en background
        encolarNotificacionTrabajadores(titulo, cuerpo)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Notificaciones de STOCK → para el TRABAJADOR / ADMIN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Muestra una notificación local de alerta cuando un repuesto baja del stock mínimo.
     * Se llama automáticamente al descontar inventario al finalizar un servicio.
     */
    fun notificarStockBajo(repuesto: Repuesto) {
        val titulo = "⚠️ Stock bajo: ${repuesto.nombre}"
        val cuerpo = "Quedan ${repuesto.cantidad} unidad(es) de \"${repuesto.nombre}\" " +
                     "(mínimo configurado: ${repuesto.stockMinimo}). " +
                     "Revisa el inventario y realiza un pedido pronto."

        MotoStockFirebaseMessagingService.mostrarNotificacionLocal(
            context   = context,
            titulo    = titulo,
            cuerpo    = cuerpo,
            channelId = MotoStockFirebaseMessagingService.CHANNEL_STOCK,
            notifId   = ("stock_${repuesto.id}").hashCode()
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internos
    // ─────────────────────────────────────────────────────────────────────────

    private suspend fun enviarNotificacionACliente(cita: Cita, titulo: String, cuerpo: String) {
        // CORRECCIÓN BUG: Se eliminó la llamada a mostrarNotificacionLocal() aquí.
        // Este código se ejecuta en el dispositivo del TRABAJADOR; mostrar la notificación
        // localmente la hacía aparecer en el teléfono del trabajador en vez del cliente.
        //
        // Solo encolamos en fcm_queue → la Cloud Function lee el fcmToken del cliente
        // en Firestore y le envía el push a SU dispositivo. Cuando el cliente tenga
        // la app abierta, su propio onMessageReceived mostrará la notificación.
        if (cita.clienteUid.isNotBlank()) {
            encolarNotificacionParaUsuario(
                destinatarioUid = cita.clienteUid,
                titulo          = titulo,
                cuerpo          = cuerpo,
                canal           = MotoStockFirebaseMessagingService.CHANNEL_CITAS
            )
        }
    }

    /**
     * Escribe en /fcm_queue un documento que la Cloud Function procesará
     * para enviar un push al usuario especificado.
     */
    private suspend fun encolarNotificacionParaUsuario(
        destinatarioUid: String,
        titulo: String,
        cuerpo: String,
        canal: String
    ) {
        try {
            // Obtener token FCM del destinatario
            val doc = firestore.collection("usuarios").document(destinatarioUid).get().await()
            val token = doc.getString("fcmToken") ?: return

            firestore.collection("fcm_queue").add(
                mapOf(
                    "token"     to token,
                    "title"     to titulo,
                    "body"      to cuerpo,
                    "channel"   to canal,
                    "timestamp" to System.currentTimeMillis(),
                    "processed" to false
                )
            ).await()
        } catch (_: Exception) {
            // Si falla el encolado, la notificación local ya se mostró; no es crítico
        }
    }

    /** Encola una notificación broadcast para TODOS los trabajadores. */
    private suspend fun encolarNotificacionTrabajadores(titulo: String, cuerpo: String) {
        try {
            firestore.collection("fcm_queue").add(
                mapOf(
                    "topic"     to "trabajadores",   // Cloud Function hace subscribe/send por topic
                    "title"     to titulo,
                    "body"      to cuerpo,
                    "channel"   to MotoStockFirebaseMessagingService.CHANNEL_CITAS,
                    "timestamp" to System.currentTimeMillis(),
                    "processed" to false
                )
            ).await()
        } catch (_: Exception) {}
    }
}
