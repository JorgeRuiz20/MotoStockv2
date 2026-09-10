package com.taller.motostock.core.database.database

import app.cash.sqldelight.db.SqlDriver
import com.taller.motostock.core.database.MotoStockDb
import com.taller.motostock.core.database.dao.*

class MotoStockDatabase(driver: SqlDriver) {
    val db: MotoStockDb = MotoStockDb(driver)

    val citaDao: CitaDao = SqlDelightCitaDao(db)
    val motoDao: MotoDao = SqlDelightMotoDao(db)
    val repuestoDao: RepuestoDao = SqlDelightRepuestoDao(db)
    val servicioDao: ServicioDao = SqlDelightServicioDao(db)

    companion object {
        const val DATABASE_NAME = "motostock_db"
    }
}
