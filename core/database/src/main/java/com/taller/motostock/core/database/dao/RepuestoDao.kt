package com.taller.motostock.core.database.dao

import androidx.room.*
import com.taller.motostock.core.database.entity.RepuestoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepuestoDao {
    @Query("SELECT * FROM repuestos ORDER BY nombre ASC")
    fun getAll(): Flow<List<RepuestoEntity>>

    @Query("SELECT * FROM repuestos WHERE cantidad <= stockMinimo ORDER BY cantidad ASC")
    fun getStockBajo(): Flow<List<RepuestoEntity>>

    @Query("SELECT * FROM repuestos WHERE id = :id")
    suspend fun getById(id: String): RepuestoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repuesto: RepuestoEntity)

    @Update
    suspend fun update(repuesto: RepuestoEntity)

    @Query("DELETE FROM repuestos WHERE id = :id")
    suspend fun delete(id: String)
}
