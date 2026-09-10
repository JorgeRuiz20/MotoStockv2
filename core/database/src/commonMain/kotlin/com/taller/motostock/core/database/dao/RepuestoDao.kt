package com.taller.motostock.core.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.taller.motostock.core.database.MotoStockDb
import com.taller.motostock.core.database.entity.RepuestoEntity
import com.taller.motostock.core.database.entity.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface RepuestoDao {
    fun getAll(): Flow<List<RepuestoEntity>>
    fun getStockBajo(): Flow<List<RepuestoEntity>>
    suspend fun getById(id: String): RepuestoEntity?
    suspend fun insert(repuesto: RepuestoEntity)
    suspend fun update(repuesto: RepuestoEntity)
    suspend fun delete(id: String)
}

class SqlDelightRepuestoDao(private val db: MotoStockDb) : RepuestoDao {
    private val queries = db.repuestoQueries

    override fun getAll(): Flow<List<RepuestoEntity>> =
        queries.getAll().asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun getStockBajo(): Flow<List<RepuestoEntity>> =
        queries.getStockBajo().asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override suspend fun getById(id: String): RepuestoEntity? =
        queries.getById(id).executeAsOneOrNull()?.toEntity()

    override suspend fun insert(repuesto: RepuestoEntity) {
        queries.insert(
            id = repuesto.id,
            nombre = repuesto.nombre,
            categoria = repuesto.categoria,
            cantidad = repuesto.cantidad.toLong(),
            precioCompra = repuesto.precioCompra,
            precioVenta = repuesto.precioVenta,
            stockMinimo = repuesto.stockMinimo.toLong(),
            proveedor = repuesto.proveedor,
            fechaActualizacion = repuesto.fechaActualizacion
        )
    }

    override suspend fun update(repuesto: RepuestoEntity) {
        insert(repuesto)
    }

    override suspend fun delete(id: String) {
        queries.delete(id)
    }
}
