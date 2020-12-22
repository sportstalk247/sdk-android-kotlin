package com.sportstalk.datamodels

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ConstantLocale")
object DateUtils {
    private val utcTz by lazy { TimeZone.getTimeZone("UTC") }
    private val isoDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.getDefault())
                .apply { timeZone = utcTz }
    }

    fun toUtcISODateTime(ts: Long): String =
            toUtcISODateTime(Date(ts))

    fun toUtcISODateTime(date: Date): String =
            isoDateFormat.format(date)
}
