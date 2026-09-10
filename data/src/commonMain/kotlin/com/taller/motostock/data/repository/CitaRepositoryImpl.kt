package com.taller.motostock.data.repository

import com.taller.motostock.core.database.dao.CitaDao
import com.taller.motostock.core.database.entity.toDomain
import com.taller.motostock.core.database.entity.toEntity
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.EstadoCita
import com.taller.motostock.core.domain.repository.CitaRepository
import com.taller.motostock.core.domain.util.DateTimeUtil
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CitaRepositoryImpl(
    private val dao: CitaDao,
    private val firestore: FirebaseFirestore = Firebase.firestore
) : CitaRepository {
    private val collection = firestore.collection("citas")
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        // Firestore es la fuente remota; sincronizamos reactivamente hacia el DAO local
        syncScope.launch {
            runCatching {
                collection.snapshots.collect { snapshot ->
                    val citas = snapshot.documents.mapNotNull { doc ->
                        runCatching {
                            val id = doc.id
                            val estadoStr = runCatching { doc.get<String>("estado") }.getOrDefault("PENDIENTE")
                            val estado = runCatching { EstadoCita.valueOf(estadoStr) }.getOrDefault(EstadoCita.PENDIENTE)
                            Cita(
                                id = id,
                                placa = runCatching { doc.get<String>("placa") }.getOrDefault(""),
                                propietario = runCatching { doc.get<String>("propietario") }.getOrDefault(""),
                                telefono = runCatching { doc.get<String>("telefono") }.getOrDefault(""),
                                modelo = runCatching { doc.get<String>("modelo") }.getOrDefault(""),
                                fechaIngreso = runCatching { doc.get<Long>("fechaIngreso") }.getOrDefault(0L),
                                horaIngreso = runCatching { doc.get<String>("horaIngreso") }.getOrDefault(""),
                                horaDeseada = runCatching { doc.get<String>("horaDeseada") }.getOrDefault(""),
                                tipoServicio = runCatching { doc.get<String>("tipoServicio") }.getOrDefault(""),
                                descripcion = runCatching { doc.get<String>("descripcion") }.getOrDefault(""),
                                estado = estado,
                                fechaSalida = runCatching { doc.get<Long?>("fechaSalida") }.getOrNull(),
                                horaSalida = runCatching { doc.get<String>("horaSalida") }.getOrDefault(""),
                                motivoRechazo = runCatching { doc.get<String>("motivoRechazo") }.getOrDefault(""),
                                motivoCancelacion = runCatching { doc.get<String>("motivoCancelacion") }.getOrDefault(""),
                                clienteUid = runCatching { doc.get<String>("clienteUid") }.getOrDefault(""),
                                clienteEmail = runCatching { doc.get<String>("clienteEmail") }.getOrDefault(""),
                                repuestosUsadosJson = runCatching { doc.get<String>("repuestosUsadosJson") }.getOrDefault(""),
                                costoServicio = runCatching { doc.get<Double>("costoServicio") }.getOrDefault(0.0)
                            )
                        }.getOrNull()
                    }
                    dao.replaceAll(citas.map { it.toEntity() })
                }
            }
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
        val newId = if (cita.id.isBlank()) "${DateTimeUtil.currentTimeMillis()}-${(100000..999999).random()}" else cita.id
        val newCita = cita.copy(id = newId)
        dao.insert(newCita.toEntity())
        runCatching {
            collection.document(newCita.id).set(newCita.toFirestoreMap())
        }
    }

    override suspend fun update(cita: Cita) {
        dao.update(cita.toEntity())
        runCatching {
            collection.document(cita.id).set(cita.toFirestoreMap())
        }
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
        runCatching {
            collection.document(id).delete()
        }
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
}
