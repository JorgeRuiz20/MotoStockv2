package com.taller.motostock.core.domain.util

expect object DateTimeUtil {
    fun currentTimeMillis(): Long
    fun formatTimeHHmm(timeMillis: Long): String
}

