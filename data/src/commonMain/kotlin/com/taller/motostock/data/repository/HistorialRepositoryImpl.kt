package com.taller.motostock.data.repository

import com.taller.motostock.core.database.dao.MotoDao
import com.taller.motostock.core.database.dao.ServicioDao
import com.taller.motostock.core.database.entity.toDomain
import com.taller.motostock.core.database.entity.toEntity
import com.taller.motostock.core.domain.model.Moto
import com.taller.motostock.core.domain.model.ServicioHistorial
import com.taller.motostock.core.domain.repository.HistorialRepository
import com.taller.motostock.core.domain.util.DateTimeUtil
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistorialRepositoryImpl(
    private val servicioDao: ServicioDao,
    private val motoDao: MotoDao,
    private val firestore: FirebaseFirestore = Firebase.firestore
) : HistorialRepository {

    override fun getByPlaca(placa: String): Flow<List<ServicioHistorial>> =
        servicioDao.getByPlaca(placa).map { it.map { entity -> entity.toDomain() } }

    override fun getAll(): Flow<List<ServicioHistorial>> =
        servicioDao.getAll().map { it.map { entity -> entity.toDomain() } }

    override suspend fun save(servicio: ServicioHistorial) {
        val newId = if (servicio.id.isBlank()) "${DateTimeUtil.currentTimeMillis()}-${(100000..999999).random()}" else servicio.id
        val newService = servicio.copy(id = newId)
        servicioDao.insert(newService.toEntity())
        runCatching {
            firestore.collection("historial").document(newService.id).set(newService.toMap())
        }
    }

    override suspend fun update(servicio: ServicioHistorial) {
        servicioDao.update(servicio.toEntity())
        runCatching {
            firestore.collection("historial").document(servicio.id).set(servicio.toMap())
        }
    }

    override suspend fun getMoto(placa: String): Moto? = motoDao.getByPlaca(placa)?.toDomain()

    override suspend fun saveMoto(moto: Moto) {
        motoDao.insert(moto.toEntity())
        runCatching {
            firestore.collection("motos").document(moto.placa).set(moto.toMap())
        }
    }

    override suspend fun buscarPlacaEnApi(placa: String): Result<Moto> =
        Result.success(Moto(id = placa, placa = placa))

    private fun ServicioHistorial.toMap(): Map<String, Any?> = mapOf(
        "id" to id, "placa" to placa, "fechaIngreso" to fechaIngreso,
        "fechaSalida" to fechaSalida, "descripcionProblema" to descripcionProblema,
        "trabajoRealizado" to trabajoRealizado, "kilometraje" to kilometraje,
        "costoManoObra" to costoManoObra, "estado" to estado.name,
        "tecnico" to tecnico, "observaciones" to observaciones
    )

    private fun Moto.toMap(): Map<String, Any?> = mapOf(
        "id" to id, "placa" to placa, "marca" to marca, "modelo" to modelo,
        "anio" to anio, "color" to color, "propietario" to propietario,
        "telefono" to telefono, "cilindraje" to cilindraje, "vin" to vin
    )
}
