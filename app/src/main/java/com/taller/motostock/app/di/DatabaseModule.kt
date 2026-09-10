package com.taller.motostock.app.di

import android.content.Context
import com.taller.motostock.core.database.DatabaseDriverFactory
import com.taller.motostock.core.database.dao.CitaDao
import com.taller.motostock.core.database.dao.MotoDao
import com.taller.motostock.core.database.dao.RepuestoDao
import com.taller.motostock.core.database.dao.ServicioDao
import com.taller.motostock.core.database.database.MotoStockDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MotoStockDatabase {
        val driver = DatabaseDriverFactory(context).createDriver()
        return MotoStockDatabase(driver)
    }

    @Provides fun provideRepuestoDao(db: MotoStockDatabase): RepuestoDao = db.repuestoDao
    @Provides fun provideMotoDao(db: MotoStockDatabase): MotoDao = db.motoDao
    @Provides fun provideServicioDao(db: MotoStockDatabase): ServicioDao = db.servicioDao
    @Provides fun provideCitaDao(db: MotoStockDatabase): CitaDao = db.citaDao
}
