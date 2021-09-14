package com.amarchaud.travelcar.utils.extensions

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.Locale

fun LocalDate.toShortDate(): String = this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault()))
fun LocalDate.toLongDate(): String = this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.getDefault()))
fun LocalDate.toMilliseconds(): Long = ZonedDateTime.of(this.atStartOfDay(), ZoneId.systemDefault()).toInstant().toEpochMilli()
fun nowToMilliseconds() : Long {
    val now = LocalDate.now()
    val local = LocalDateTime.of(now.year, now.month, 1, 0, 0)
    return local.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
