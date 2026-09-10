package com.taller.motostock.core.domain.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual object DateTimeUtil {
    actual fun currentTimeMillis(): Long = System.currentTimeMillis()
    actual fun formatTimeHHmm(timeMillis: Long): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timeMillis))
}

