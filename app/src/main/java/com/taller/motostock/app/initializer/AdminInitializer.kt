package com.taller.motostock.app.initializer

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.taller.motostock.core.domain.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Inicializador de cuenta de Administrador para pruebas y desarrollo.
 *
 * Utiliza una instancia secundaria de [FirebaseApp] para registrar/verificar el usuario
 * en Firebase Auth y Firestore SIN afectar la sesión activa principal ([FirebaseAuth.getInstance()]).
 * Esto garantiza que la app NUNCA inicie sesión automáticamente como admin al abrirse.
 */
@Singleton
class AdminInitializer @Inject constructor() {

    companion object {
        private const val TAG = "AdminInitializer"
        const val ADMIN_EMAIL = "admin.test@motostock.com"
        const val ADMIN_PASSWORD = "admin123"
        const val ADMIN_NOMBRE = "Administrador"
        private const val SECONDARY_APP_NAME = "MotoStockAdminSeeder"
    }

    /**
     * Inicia la creación y sincronización del usuario admin en segundo plano.
     */
    fun initialize(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                seedAdmin(context.applicationContext)
            } catch (e: Exception) {
                Log.e(TAG, "Error durante la inicialización del admin de prueba: ${e.message}", e)
            }
        }
    }

    private suspend fun seedAdmin(context: Context) {
        val defaultApp = try {
            FirebaseApp.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseApp por defecto no está listo: ${e.message}")
            return
        }

        // Creamos o recuperamos la instancia secundaria aislada
        val secondaryApp = FirebaseApp.getApps(context).firstOrNull { it.name == SECONDARY_APP_NAME }
            ?: FirebaseApp.initializeApp(context, defaultApp.options, SECONDARY_APP_NAME)

        val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)
        val secondaryFirestore = FirebaseFirestore.getInstance(secondaryApp)

        var adminUid: String? = null

        try {
            // Intentar crear el usuario en Firebase Auth
            val authResult = secondaryAuth.createUserWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD).await()
            adminUid = authResult.user?.uid
            Log.i(TAG, "Usuario admin creado exitosamente en Auth con UID: $adminUid")
        } catch (e: FirebaseAuthUserCollisionException) {
            // El usuario ya existe en Auth, iniciamos sesión SOLO en la instancia secundaria para obtener su UID
            Log.i(TAG, "El usuario admin ($ADMIN_EMAIL) ya existe. Obteniendo UID...")
            try {
                val signInResult = secondaryAuth.signInWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD).await()
                adminUid = signInResult.user?.uid
            } catch (signInEx: Exception) {
                Log.w(TAG, "No se pudo autenticar usuario admin existente en secondaryAuth: ${signInEx.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear o verificar admin en Firebase Auth: ${e.message}")
        }

        // Si obtuvimos el UID (creado o existente), aseguramos el rol de administrador en Firestore
        if (adminUid != null) {
            try {
                val adminData = mapOf(
                    "uid" to adminUid,
                    "email" to ADMIN_EMAIL,
                    "nombre" to ADMIN_NOMBRE,
                    "role" to UserRole.ADMINISTRADOR.value,
                    "creadoEn" to System.currentTimeMillis()
                )
                try {
                    secondaryFirestore.collection("usuarios")
                        .document(adminUid)
                        .set(adminData, SetOptions.merge())
                        .await()
                } catch (ex: Exception) {
                    FirebaseFirestore.getInstance().collection("usuarios")
                        .document(adminUid)
                        .set(adminData, SetOptions.merge())
                        .await()
                }

                Log.i(TAG, "Documento de Firestore confirmado con rol 'administrador' para UID: $adminUid")
            } catch (e: Exception) {
                Log.e(TAG, "Error guardando documento admin en Firestore: ${e.message}")
            }
        }

        // Cerramos la sesión en la instancia secundaria para no dejar tokens activos
        try {
            secondaryAuth.signOut()
        } catch (e: Exception) {
            // Ignorar
        }
    }
}
