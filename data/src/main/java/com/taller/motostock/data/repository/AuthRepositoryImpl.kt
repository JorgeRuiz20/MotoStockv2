package com.taller.motostock.data.repository

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.taller.motostock.core.domain.model.UserRole
import com.taller.motostock.core.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : AuthRepository {

    companion object {
        private const val WORKER_CREATOR_APP = "MotoStockWorkerCreator"
        private const val ADMIN_EMAIL = "admin@motostock.com"
        private const val ADMIN_PASSWORD = "123456"
        private const val LEGACY_ADMIN_EMAIL = "admin.test@motostock.com"
        private const val LEGACY_ADMIN_PASSWORD = "admin123"
    }
    override fun usuarioActual(): String? = firebaseAuth.currentUser?.email
    override fun usuarioActualUid(): String? = firebaseAuth.currentUser?.uid
    override fun haySesionActiva(): Boolean = firebaseAuth.currentUser != null

    override suspend fun getRolUsuarioActual(): UserRole {
        val uid = usuarioActualUid() ?: return UserRole.CLIENTE
        return runCatching {
            val role = firestore.collection("usuarios").document(uid).get().await()
                .getString("role") ?: UserRole.CLIENTE.value
            UserRole.fromValue(role)
        }.getOrDefault(UserRole.CLIENTE)
    }

    override suspend fun getNombreUsuarioActual(): String {
        val uid = usuarioActualUid() ?: return ""
        return runCatching {
            firestore.collection("usuarios").document(uid).get().await()
                .getString("nombre") ?: (firebaseAuth.currentUser?.displayName ?: "")
        }.getOrDefault(firebaseAuth.currentUser?.displayName ?: "")
    }

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val normalizedEmail = email.trim()
        val isAdminTestAccount = isAdminTestAccount(normalizedEmail, password)
        try {
            firebaseAuth.signInWithEmailAndPassword(normalizedEmail, password).await()
            if (isAdminTestAccount) {
                firebaseAuth.currentUser?.uid?.let { ensureAdminProfile(it, normalizedEmail) }
            }
        } catch (e: Exception) {
            // Si es el admin de prueba y falló porque aún no existía en Firebase Auth, lo creamos
            if (isAdminTestAccount) {
                val createResult = firebaseAuth.createUserWithEmailAndPassword(normalizedEmail, password).await()
                val uid = createResult.user?.uid ?: error("No se pudo obtener el UID del admin")
                ensureAdminProfile(uid, normalizedEmail)
            } else {
                throw e
            }
        }
    }

    override suspend fun loginConGoogle(idToken: String): Result<Unit> = runCatching {
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
        val user = firebaseAuth.currentUser ?: return@runCatching
        val profile = firestore.collection("usuarios").document(user.uid)
        if (!profile.get().await().exists()) {
            profile.set(
                mapOf(
                    "uid" to user.uid,
                    "email" to (user.email ?: ""),
                    "nombre" to (user.displayName ?: ""),
                    "role" to UserRole.CLIENTE.value,
                    "creadoEn" to System.currentTimeMillis()
                )
            ).await()
        }
    }

    override suspend fun registrarCliente(email: String, password: String, nombre: String): Result<Unit> =
        crearUsuario(email, password, nombre, UserRole.CLIENTE)

    override suspend fun crearTrabajador(email: String, password: String, nombre: String): Result<Unit> = runCatching {
        check(haySesionActiva()) { "Debes iniciar sesión como administrador para crear trabajadores." }
        check(getRolUsuarioActual() == UserRole.ADMINISTRADOR) {
            "Solo un administrador puede crear trabajadores."
        }

        val defaultApp = FirebaseApp.getInstance()
        val creatorApp = FirebaseApp.getApps(context)
            .firstOrNull { it.name == WORKER_CREATOR_APP }
            ?: FirebaseApp.initializeApp(context, defaultApp.options, WORKER_CREATOR_APP)
        val creatorAuth = FirebaseAuth.getInstance(creatorApp)

        try {
            val result = creatorAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: error("No se pudo obtener el UID del trabajador")

            // La escritura se hace con la sesión primaria del admin; la sesión del
            // trabajador nunca reemplaza la sesión activa en la app.
            firestore.collection("usuarios").document(uid).set(
                mapOf(
                    "uid" to uid,
                    "email" to email,
                    "nombre" to nombre,
                    "role" to UserRole.TRABAJADOR.value,
                    "creadoEn" to System.currentTimeMillis()
                )
            ).await()
        } catch (error: Exception) {
            // Evita dejar una cuenta de Auth huérfana si falla la creación del perfil.
            creatorAuth.currentUser?.delete()?.await()
            throw error
        } finally {
            creatorAuth.signOut()
        }
    }

    private suspend fun crearUsuario(email: String, password: String, nombre: String, role: UserRole): Result<Unit> = runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: error("No se pudo obtener el usuario creado")
        firestore.collection("usuarios").document(uid).set(
            mapOf("uid" to uid, "email" to email, "nombre" to nombre, "role" to role.value, "creadoEn" to System.currentTimeMillis())
        ).await()
    }

    private fun isAdminTestAccount(email: String, password: String): Boolean =
        (email.equals(ADMIN_EMAIL, ignoreCase = true) && password == ADMIN_PASSWORD) ||
                (email.equals(LEGACY_ADMIN_EMAIL, ignoreCase = true) && password == LEGACY_ADMIN_PASSWORD)

    private suspend fun ensureAdminProfile(uid: String, email: String) {
        firestore.collection("usuarios").document(uid).set(
            mapOf(
                "uid" to uid,
                "email" to email,
                "nombre" to "Administrador",
                "role" to UserRole.ADMINISTRADOR.value,
                "creadoEn" to System.currentTimeMillis()
            ),
            com.google.firebase.firestore.SetOptions.merge()
        ).await()
    }

    override fun logout() = firebaseAuth.signOut()
}
