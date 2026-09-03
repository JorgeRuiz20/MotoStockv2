package com.taller.motostock.app

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import com.taller.motostock.core.common.MotoStockFirebaseMessagingService
import com.taller.motostock.app.initializer.AdminInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MotoStockApp : Application() {

    @Inject
    lateinit var adminInitializer: AdminInitializer

    override fun onCreate() {
        super.onCreate()

        // Inicializar admin de prueba (sin iniciar sesión automáticamente)
        adminInitializer.initialize(this)

        // Crear canales de notificación (Android 8+)
        MotoStockFirebaseMessagingService.crearCanales(this)

        // Obtener token FCM actual y guardarlo cuando haya usuario autenticado.
        // (Al hacer login, LoginFragment también llama a guardarTokenSiHayUsuario)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            MotoStockFirebaseMessagingService.guardarTokenSiHayUsuario(token)
        }
    }
}
