package com.taller.motostock.app.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.taller.motostock.core.database.dao.CitaDao
import com.taller.motostock.core.database.dao.MotoDao
import com.taller.motostock.core.database.dao.RepuestoDao
import com.taller.motostock.core.database.dao.ServicioDao
import com.taller.motostock.core.domain.repository.AuthRepository
import com.taller.motostock.core.domain.repository.CitaRepository
import com.taller.motostock.core.domain.repository.HistorialRepository
import com.taller.motostock.core.domain.repository.RepuestoRepository
import com.taller.motostock.data.repository.AuthRepositoryImpl
import com.taller.motostock.data.repository.CitaRepositoryImpl
import com.taller.motostock.data.repository.HistorialRepositoryImpl
import com.taller.motostock.data.repository.RepuestoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRepuestoRepository(dao: RepuestoDao): RepuestoRepository =
        RepuestoRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideHistorialRepository(servicioDao: ServicioDao, motoDao: MotoDao): HistorialRepository =
        HistorialRepositoryImpl(servicioDao, motoDao)

    @Provides
    @Singleton
    fun provideCitaRepository(dao: CitaDao): CitaRepository =
        CitaRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        @ApplicationContext context: Context
    ): AuthRepository = AuthRepositoryImpl(firebaseAuth, firestore, context)
}
