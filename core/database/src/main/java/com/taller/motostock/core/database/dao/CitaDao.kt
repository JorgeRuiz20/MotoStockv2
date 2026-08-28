package com.taller.motostock.core.database.dao

import androidx.room.*
import com.taller.motostock.core.database.entity.CitaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CitaDao {
    @Query("SELECT * FROM citas ORDER BY fechaIngreso DESC")
    fun getAll(): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE estado = 'PENDIENTE' ORDER BY fechaIngreso DESC")
    fun getPendientes(): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE estado = 'ACEPTADA' ORDER BY fechaIngreso DESC")
    fun getAceptadas(): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE estado = 'EN_PROCESO' ORDER BY fechaIngreso DESC")
    fun getEnProceso(): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE estado IN ('ACEPTADA', 'EN_PROCESO') ORDER BY fechaIngreso DESC")
    fun getAceptadasYEnProceso(): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE estado = 'EN_PROCESO' ORDER BY fechaIngreso DESC")
    fun getEnTaller(): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE estado = 'FINALIZADO' ORDER BY fechaIngreso DESC")
    fun getFinalizados(): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE UPPER(placa) LIKE '%' || UPPER(:placa) || '%' AND estado = 'FINALIZADO' ORDER BY fechaIngreso DESC")
    fun buscarFinalizadosPorPlaca(placa: String): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE estado = 'FINALIZADO' AND fechaIngreso BETWEEN :inicio AND :fin ORDER BY fechaIngreso DESC")
    fun getFinalizadosPorFecha(inicio: Long, fin: Long): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE clienteUid = :uid ORDER BY fechaIngreso DESC")
    fun getCitasPorCliente(uid: String): Flow<List<CitaEntity>>

    @Query("SELECT * FROM citas WHERE clienteEmail = :email ORDER BY fechaIngreso DESC")
    fun getCitasPorEmail(email: String): Flow<List<CitaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cita: CitaEntity)

    @Update
    suspend fun update(cita: CitaEntity)

    @Query("DELETE FROM citas WHERE id = :id")
    suspend fun delete(id: String)
}
