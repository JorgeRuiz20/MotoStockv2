package com.taller.motostock.core.network.api

import com.taller.motostock.core.network.dto.VehicleApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Servicio multiplataforma para consulta de placas con CarsXE API usando Ktor Client.
 */
class PlacaApiService(
    private val client: HttpClient = createPlatformHttpClient()
) {
    companion object {
        private const val BASE_URL = "https://api.carsxe.com"
    }

    suspend fun buscarPorPlaca(
        apiKey: String,
        plate: String,
        country: String = "PE"
    ): VehicleApiResponse {
        return client.get("$BASE_URL/platedecoder") {
            parameter("key", apiKey)
            parameter("plate", plate)
            parameter("country", country)
        }.body()
    }
}

