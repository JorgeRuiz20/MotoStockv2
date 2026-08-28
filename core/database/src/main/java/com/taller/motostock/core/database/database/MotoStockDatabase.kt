package com.taller.motostock.core.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.taller.motostock.core.database.dao.*
import com.taller.motostock.core.database.entity.*

@Database(
    entities = [
        RepuestoEntity::class,
        MotoEntity::class,
        ServicioEntity::class,
        CitaEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MotoStockDatabase : RoomDatabase() {
    abstract fun repuestoDao(): RepuestoDao
    abstract fun motoDao(): MotoDao
    abstract fun servicioDao(): ServicioDao
    abstract fun citaDao(): CitaDao

    companion object {
        const val DATABASE_NAME = "motostock_db"
    }
}
