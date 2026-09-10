package com.taller.motostock.data.repository

import com.taller.motostock.core.database.dao.RepuestoDao
import com.taller.motostock.core.database.entity.toDomain
import com.taller.motostock.core.database.entity.toEntity
import com.taller.motostock.core.domain.model.Repuesto
import com.taller.motostock.core.domain.repository.RepuestoRepository
import com.taller.motostock.core.domain.util.DateTimeUtil
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepuestoRepositoryImpl(
    private val dao: RepuestoDao,
    private val firestore: FirebaseFirestore = Firebase.firestore
) : RepuestoRepository {

    private val collection = firestore.collection("repuestos")

    override fun getAll(): Flow<List<Repuesto>> = dao.getAll().map { entities -> entities.map { it.toDomain() } }
    override fun getStockBajo(): Flow<List<Repuesto>> = dao.getStockBajo().map { entities -> entities.map { it.toDomain() } }
    override suspend fun getById(id: String): Repuesto? = dao.getById(id)?.toDomain()

    override suspend fun save(repuesto: Repuesto) {
        val newId = if (repuesto.id.isBlank()) "${DateTimeUtil.currentTimeMillis()}-${(100000..999999).random()}" else repuesto.id
        val newRepuesto = repuesto.copy(id = newId)
        dao.insert(newRepuesto.toEntity())
        runCatching {
            collection.document(newRepuesto.id).set(newRepuesto.toFirestoreMap())
        }
    }

    override suspend fun update(repuesto: Repuesto) {
        val updated = repuesto.copy(fechaActualizacion = DateTimeUtil.currentTimeMillis())
        dao.update(updated.toEntity())
        runCatching {
            collection.document(updated.id).set(updated.toFirestoreMap())
        }
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
        runCatching {
            collection.document(id).delete()
        }
    }

    private fun Repuesto.toFirestoreMap() = mapOf(
        "id" to id, "nombre" to nombre, "categoria" to categoria, "cantidad" to cantidad,
        "precioCompra" to precioCompra, "precioVenta" to precioVenta,
        "stockMinimo" to stockMinimo, "proveedor" to proveedor,
        "fechaActualizacion" to fechaActualizacion
    )
}
