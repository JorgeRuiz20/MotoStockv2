package com.taller.motostock.app

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import com.taller.motostock.core.common.MotoStockFirebaseMessagingService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MotoStockApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Crear canales de notificación (Android 8+)
        MotoStockFirebaseMessagingService.crearCanales(this)

        // Obtener token FCM actual y guardarlo cuando haya usuario autenticado.
        // (Al hacer login, LoginFragment también llama a guardarTokenSiHayUsuario)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            MotoStockFirebaseMessagingService.guardarTokenSiHayUsuario(token)
        }
    }
}
