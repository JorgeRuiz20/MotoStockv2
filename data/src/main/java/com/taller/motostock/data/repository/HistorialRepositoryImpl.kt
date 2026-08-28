package com.taller.motostock.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.taller.motostock.core.database.dao.MotoDao
import com.taller.motostock.core.database.dao.ServicioDao
import com.taller.motostock.core.database.entity.toDomain
import com.taller.motostock.core.database.entity.toEntity
import com.taller.motostock.core.domain.model.Moto
import com.taller.motostock.core.domain.model.ServicioHistorial
import com.taller.motostock.core.domain.repository.HistorialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class HistorialRepositoryImpl @Inject constructor(
    private val servicioDao: ServicioDao,
    private val motoDao: MotoDao,
    private val firestore: FirebaseFirestore
) : HistorialRepository {
    override fun getByPlaca(placa: String): Flow<List<ServicioHistorial>> =
        servicioDao.getByPlaca(placa).map { it.map { entity -> entity.toDomain() } }

    override fun getAll(): Flow<List<ServicioHistorial>> =
        servicioDao.getAll().map { it.map { entity -> entity.toDomain() } }

    override suspend fun save(servicio: ServicioHistorial) {
        val newService = servicio.copy(id = UUID.randomUUID().toString())
        servicioDao.insert(newService.toEntity())
        firestore.collection("historial").document(newService.id).set(newService).await()
    }

    override suspend fun update(servicio: ServicioHistorial) {
        servicioDao.update(servicio.toEntity())
        firestore.collection("historial").document(servicio.id).set(servicio).await()
    }

    override suspend fun getMoto(placa: String): Moto? = motoDao.getByPlaca(placa)?.toDomain()

    override suspend fun saveMoto(moto: Moto) {
        motoDao.insert(moto.toEntity())
        firestore.collection("motos").document(moto.placa).set(moto).await()
    }

    override suspend fun buscarPlacaEnApi(placa: String): Result<Moto> =
        Result.success(Moto(id = placa, placa = placa))
}
