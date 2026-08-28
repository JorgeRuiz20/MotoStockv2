package com.taller.motostock.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.taller.motostock.core.database.dao.RepuestoDao
import com.taller.motostock.core.database.entity.toDomain
import com.taller.motostock.core.database.entity.toEntity
import com.taller.motostock.core.domain.model.Repuesto
import com.taller.motostock.core.domain.repository.RepuestoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class RepuestoRepositoryImpl @Inject constructor(
    private val dao: RepuestoDao,
    private val firestore: FirebaseFirestore
) : RepuestoRepository {

    private val collection = firestore.collection("repuestos")

    override fun getAll(): Flow<List<Repuesto>> = dao.getAll().map { entities -> entities.map { it.toDomain() } }
    override fun getStockBajo(): Flow<List<Repuesto>> = dao.getStockBajo().map { entities -> entities.map { it.toDomain() } }
    override suspend fun getById(id: String): Repuesto? = dao.getById(id)?.toDomain()

    override suspend fun save(repuesto: Repuesto) {
        val newRepuesto = repuesto.copy(id = UUID.randomUUID().toString())
        dao.insert(newRepuesto.toEntity())
        collection.document(newRepuesto.id).set(newRepuesto.toFirestoreMap()).await()
    }

    override suspend fun update(repuesto: Repuesto) {
        val updated = repuesto.copy(fechaActualizacion = System.currentTimeMillis())
        dao.update(updated.toEntity())
        collection.document(updated.id).set(updated.toFirestoreMap()).await()
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
        collection.document(id).delete().await()
    }

    private fun Repuesto.toFirestoreMap() = mapOf(
        "id" to id, "nombre" to nombre, "categoria" to categoria, "cantidad" to cantidad,
        "precioCompra" to precioCompra, "precioVenta" to precioVenta,
        "stockMinimo" to stockMinimo, "proveedor" to proveedor,
        "fechaActualizacion" to fechaActualizacion
    )
}
