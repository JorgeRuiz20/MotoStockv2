package com.taller.motostock.core.database.dao

import com.taller.motostock.core.database.MotoStockDb
import com.taller.motostock.core.database.entity.MotoEntity
import com.taller.motostock.core.database.entity.toEntity

interface MotoDao {
    suspend fun getByPlaca(placa: String): MotoEntity?
    suspend fun insert(moto: MotoEntity)
}

class SqlDelightMotoDao(private val db: MotoStockDb) : MotoDao {
    private val queries = db.motoQueries

    override suspend fun getByPlaca(placa: String): MotoEntity? =
        queries.getByPlaca(placa).executeAsOneOrNull()?.toEntity()

    override suspend fun insert(moto: MotoEntity) {
        queries.insert(
            id = moto.id,
            placa = moto.placa,
            marca = moto.marca,
            modelo = moto.modelo,
            anio = moto.anio.toLong(),
            color = moto.color,
            propietario = moto.propietario,
            telefono = moto.telefono,
            cilindraje = moto.cilindraje,
            vin = moto.vin
        )
    }
}
