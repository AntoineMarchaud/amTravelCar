package com.amarchaud.travelcar.utils.extensions

import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.*

fun LocalDate.toShortDate(): String = this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault()))
fun LocalDate.toLongDate(): String = this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.getDefault()))
fun LocalDate.toMilliseconds(): Long = ZonedDateTime.of(this.atStartOfDay(), ZoneId.systemDefault()).toInstant().toEpochMilli()
