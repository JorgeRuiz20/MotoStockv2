package com.taller.motostock.app.di

import com.taller.motostock.core.network.api.PlacaApiService
import com.taller.motostock.core.network.api.createPlatformHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = createPlatformHttpClient()

    @Provides
    @Singleton
    fun providePlacaApiService(client: HttpClient): PlacaApiService =
        PlacaApiService(client)
}
