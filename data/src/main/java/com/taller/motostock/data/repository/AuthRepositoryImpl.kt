package com.taller.motostock.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.taller.motostock.core.domain.model.UserRole
import com.taller.motostock.core.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
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

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
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

    override suspend fun crearTrabajador(email: String, password: String, nombre: String): Result<Unit> =
        Result.failure(UnsupportedOperationException("La creación de trabajadores debe realizarse desde un servicio administrativo seguro."))

    private suspend fun crearUsuario(email: String, password: String, nombre: String, role: UserRole): Result<Unit> = runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: error("No se pudo obtener el usuario creado")
        firestore.collection("usuarios").document(uid).set(
            mapOf("uid" to uid, "email" to email, "nombre" to nombre, "role" to role.value, "creadoEn" to System.currentTimeMillis())
        ).await()
    }

    override fun logout() = firebaseAuth.signOut()
}
