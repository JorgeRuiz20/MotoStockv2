package com.taller.motostock.core.database.dao

import androidx.room.*
import com.taller.motostock.core.database.entity.MotoEntity

@Dao
interface MotoDao {
    @Query("SELECT * FROM motos WHERE UPPER(placa) = UPPER(:placa) LIMIT 1")
    suspend fun getByPlaca(placa: String): MotoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(moto: MotoEntity)
}
