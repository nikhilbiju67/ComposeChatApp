package com.compose_chat.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun LocalDateTime.formatTime(composeChatDateFormat: ComposeChatDateFormat=ComposeChatDateFormat.DateTime): String {
    return when (composeChatDateFormat) {
        ComposeChatDateFormat.Relative -> this.toRelativeString()
        ComposeChatDateFormat.Time -> this.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        ComposeChatDateFormat.Date -> this.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        ComposeChatDateFormat.DateTime -> this.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"))
        is ComposeChatDateFormat.Custom -> this.format(
            DateTimeFormatter.ofPattern(
                composeChatDateFormat.format
            )
        )
    }
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
        else -> this.formatTime(ComposeChatDateFormat.Date)
    }
}

sealed class ComposeChatDateFormat {
    data object Relative : ComposeChatDateFormat()
    data object Time : ComposeChatDateFormat()
    data object Date : ComposeChatDateFormat()
    data object DateTime : ComposeChatDateFormat()

    data class Custom(
        val format: String
    ) : ComposeChatDateFormat()
}