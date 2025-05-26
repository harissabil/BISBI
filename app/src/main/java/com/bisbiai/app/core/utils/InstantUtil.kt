package com.bisbiai.app.core.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

fun Instant.toDdMmYyyyHhMmSs(): String {
    val tz = TimeZone.currentSystemDefault()
    val localDateTime = this.toLocalDateTime(tz)
    val date = localDateTime.date
    val time = localDateTime.time

    return date.format(LocalDate.Format {
        dayOfMonth()
        chars("-")
        monthNumber()
        chars("-")
        year()
    }) + " " + time.format(LocalTime.Format {
        hour()
        chars(":")
        minute()
    })
}