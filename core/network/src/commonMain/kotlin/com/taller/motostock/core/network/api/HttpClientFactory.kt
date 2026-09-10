package com.taller.motostock.core.network.api

import io.ktor.client.HttpClient

expect fun createPlatformHttpClient(): HttpClient

