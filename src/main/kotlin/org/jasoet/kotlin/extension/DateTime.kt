package org.jasoet.kotlin.extension

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

fun Int.toYear(): Year {
    return Year.of(this)
}

fun LocalDateTime?.isoDateFormat(): String? {
    return this?.format(DateTimeFormatter.ISO_DATE)
}

fun LocalDateTime?.isoTimeFormat(): String? {
    return this?.format(DateTimeFormatter.ISO_TIME)
}

fun LocalDateTime?.isoDateTimeFormat(): String? {
    return this?.format(DateTimeFormatter.ISO_DATE_TIME)
}

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}

fun LocalDateTime.toMilliSeconds(): Long {
    return this.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
}

@Throws(DateTimeParseException::class)
fun String.toLocalDate(pattern: String): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern))
}
