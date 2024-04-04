package com.compose_chat.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun LocalDateTime.toReadableString(format: String = "MM-dd hh:mm a"): String {
    val formatter = DateTimeFormatter.ofPattern(format)
    return this.format(formatter)
}

fun LocalDateTime.toFormatDateForDisplay(): String {
    val formatter = DateTimeFormatter.ofPattern("EEE dd MMM yyyy")
    return this.format(formatter)
}

fun LocalDateTime.toRelativeString(): String {
    val now = LocalDateTime.now()
    val daysBetween = ChronoUnit.DAYS.between(this, now)
    return when (daysBetween) {
        0L -> "today"
        1L -> "yesterday"
        in 2L..6L -> "$daysBetween days ago"
        else -> this.toFormatDateForDisplay()
    }
}