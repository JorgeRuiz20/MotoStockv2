package com.taller.motostock.core.network.api

import com.taller.motostock.core.network.dto.VehicleApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// API: CarsXE Plate Decoder - https://api.carsxe.com
// Registro gratuito en: https://api.carsxe.com/signup
// Incluye Perú en cobertura internacional
interface PlacaApiService {
    @GET("platedecoder")
    suspend fun buscarPorPlaca(
        @Query("key") apiKey: String,
        @Query("plate") plate: String,
        @Query("country") country: String = "PE"
    ): Response<VehicleApiResponse>
}
