package com.taller.motostock.app.di

import com.taller.motostock.core.domain.repository.AuthRepository
import com.taller.motostock.core.domain.repository.CitaRepository
import com.taller.motostock.core.domain.repository.HistorialRepository
import com.taller.motostock.core.domain.repository.RepuestoRepository
import com.taller.motostock.data.repository.AuthRepositoryImpl
import com.taller.motostock.data.repository.CitaRepositoryImpl
import com.taller.motostock.data.repository.HistorialRepositoryImpl
import com.taller.motostock.data.repository.RepuestoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindRepuestoRepository(impl: RepuestoRepositoryImpl): RepuestoRepository

    @Binds @Singleton
    abstract fun bindHistorialRepository(impl: HistorialRepositoryImpl): HistorialRepository

    @Binds @Singleton
    abstract fun bindCitaRepository(impl: CitaRepositoryImpl): CitaRepository

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
