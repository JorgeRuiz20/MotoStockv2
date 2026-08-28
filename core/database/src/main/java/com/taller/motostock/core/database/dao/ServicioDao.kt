package com.taller.motostock.core.database.dao

import androidx.room.*
import com.taller.motostock.core.database.entity.ServicioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicioDao {
    @Query("SELECT * FROM servicios WHERE UPPER(placa) = UPPER(:placa) ORDER BY fechaIngreso DESC")
    fun getByPlaca(placa: String): Flow<List<ServicioEntity>>

    @Query("SELECT * FROM servicios ORDER BY fechaIngreso DESC")
    fun getAll(): Flow<List<ServicioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(servicio: ServicioEntity)

    @Update
    suspend fun update(servicio: ServicioEntity)
}
