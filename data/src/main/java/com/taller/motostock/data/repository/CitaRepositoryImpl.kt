package com.taller.motostock.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.taller.motostock.core.database.dao.CitaDao
import com.taller.motostock.core.database.entity.toDomain
import com.taller.motostock.core.database.entity.toEntity
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.repository.CitaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.taller.motostock.core.domain.model.EstadoCita
import java.util.UUID
import javax.inject.Inject

class CitaRepositoryImpl @Inject constructor(
    private val dao: CitaDao,
    private val firestore: FirebaseFirestore
) : CitaRepository {
    private val collection = firestore.collection("citas")
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Firestore es la fuente compartida. Room sigue siendo la fuente que
        // consumen las pantallas, por eso reflejamos aquí cada cambio remoto.
        collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "No se pudieron sincronizar las citas", error)
                return@addSnapshotListener
            }
            val citas = snapshot?.documents?.mapNotNull { it.toDomainCita() } ?: return@addSnapshotListener
            syncScope.launch { dao.replaceAll(citas.map { it.toEntity() }) }
        }
    }

    override fun getAll(): Flow<List<Cita>> = dao.getAll().map { it.map { entity -> entity.toDomain() } }
    override fun getPendientes(): Flow<List<Cita>> = dao.getPendientes().map { it.map { entity -> entity.toDomain() } }
    override fun getAceptadas(): Flow<List<Cita>> = dao.getAceptadas().map { it.map { entity -> entity.toDomain() } }
    override fun getEnProceso(): Flow<List<Cita>> = dao.getEnProceso().map { it.map { entity -> entity.toDomain() } }
    override fun getAceptadasYEnProceso(): Flow<List<Cita>> = dao.getAceptadasYEnProceso().map { it.map { entity -> entity.toDomain() } }
    override fun getEnTaller(): Flow<List<Cita>> = dao.getEnTaller().map { it.map { entity -> entity.toDomain() } }
    override fun getFinalizados(): Flow<List<Cita>> = dao.getFinalizados().map { it.map { entity -> entity.toDomain() } }
    override fun buscarFinalizadosPorPlaca(placa: String): Flow<List<Cita>> = dao.buscarFinalizadosPorPlaca(placa).map { it.map { entity -> entity.toDomain() } }
    override fun getFinalizadosPorFecha(inicio: Long, fin: Long): Flow<List<Cita>> = dao.getFinalizadosPorFecha(inicio, fin).map { it.map { entity -> entity.toDomain() } }
    override fun getCitasPorCliente(uid: String): Flow<List<Cita>> = dao.getCitasPorCliente(uid).map { it.map { entity -> entity.toDomain() } }
    override fun getCitasPorEmail(email: String): Flow<List<Cita>> = dao.getCitasPorEmail(email).map { it.map { entity -> entity.toDomain() } }
    override fun getCitasPorClienteOEmail(uid: String, email: String): Flow<List<Cita>> =
        dao.getCitasPorClienteOEmail(uid, email).map { it.map { entity -> entity.toDomain() } }

    override suspend fun save(cita: Cita) {
        val newCita = if (cita.id.isBlank()) cita.copy(id = UUID.randomUUID().toString()) else cita
        dao.insert(newCita.toEntity())
        collection.document(newCita.id).set(newCita.toFirestoreMap()).await()
    }

    override suspend fun update(cita: Cita) {
        dao.update(cita.toEntity())
        collection.document(cita.id).set(cita.toFirestoreMap()).await()
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
        collection.document(id).delete().await()
    }

    private fun Cita.toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id, "placa" to placa, "propietario" to propietario, "telefono" to telefono,
        "modelo" to modelo, "fechaIngreso" to fechaIngreso, "horaIngreso" to horaIngreso,
        "horaDeseada" to horaDeseada, "tipoServicio" to tipoServicio, "descripcion" to descripcion,
        "estado" to estado.name, "fechaSalida" to fechaSalida, "horaSalida" to horaSalida,
        "motivoRechazo" to motivoRechazo, "motivoCancelacion" to motivoCancelacion,
        "clienteUid" to clienteUid, "clienteEmail" to clienteEmail,
        "repuestosUsadosJson" to repuestosUsadosJson, "costoServicio" to costoServicio
    )

    private fun DocumentSnapshot.toDomainCita(): Cita? {
        val documentId = this.id
        val id = getString("id") ?: documentId.takeIf { it.isNotBlank() } ?: return null
        val estado = runCatching {
            EstadoCita.valueOf(getString("estado").orEmpty())
        }.getOrDefault(EstadoCita.PENDIENTE)
        return Cita(
            id = id,
            placa = getString("placa").orEmpty(),
            propietario = getString("propietario").orEmpty(),
            telefono = getString("telefono").orEmpty(),
            modelo = getString("modelo").orEmpty(),
            fechaIngreso = getLong("fechaIngreso") ?: 0L,
            horaIngreso = getString("horaIngreso").orEmpty(),
            horaDeseada = getString("horaDeseada").orEmpty(),
            tipoServicio = getString("tipoServicio").orEmpty(),
            descripcion = getString("descripcion").orEmpty(),
            estado = estado,
            fechaSalida = getLong("fechaSalida"),
            horaSalida = getString("horaSalida").orEmpty(),
            motivoRechazo = getString("motivoRechazo").orEmpty(),
            motivoCancelacion = getString("motivoCancelacion").orEmpty(),
            clienteUid = getString("clienteUid").orEmpty(),
            clienteEmail = getString("clienteEmail").orEmpty(),
            repuestosUsadosJson = getString("repuestosUsadosJson").orEmpty(),
            costoServicio = (get("costoServicio") as? Number)?.toDouble() ?: 0.0
        )
    }

    private companion object {
        const val TAG = "CitaRepository"
    }
}
