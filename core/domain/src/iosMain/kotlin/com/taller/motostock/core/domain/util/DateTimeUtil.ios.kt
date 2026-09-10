package com.taller.motostock.core.domain.util

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.dateWithTimeIntervalSince1970

actual object DateTimeUtil {
    actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
    actual fun formatTimeHHmm(timeMillis: Long): String {
        val formatter = NSDateFormatter().apply {
            dateFormat = "HH:mm"
            locale = NSLocale.currentLocale
        }
        return formatter.stringFromDate(NSDate.dateWithTimeIntervalSince1970(timeMillis / 1000.0))
    }
}

