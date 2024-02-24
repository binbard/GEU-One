package com.binbard.geu.one.ui.feed

import java.util.*

data class Feed(
    val id: Int,
    val slug: String,
    val title: String,
    val date: Date
){
    fun getDiff(): String {
        val now = Date()
        val diffMillis = now.time - date.time
        val diffMinutes = diffMillis / (1000 * 60)

        return when {
            diffMinutes < 60 -> "$diffMinutes minutes ago"
            diffMinutes < 24 * 60 -> formatTime(diffMinutes / 60, "hours")
            diffMinutes < 7 * 24 * 60 -> formatTime(diffMinutes / (24 * 60), "days")
            diffMinutes < 30 * 24 * 60 -> formatTime(diffMinutes / (7 * 24 * 60), "weeks")
            diffMinutes < 365 * 24 * 60 -> formatTime(diffMinutes / (30 * 24 * 60), "months")
            else -> formatTime(diffMinutes / (365 * 24 * 60), "year")
        }
    }

    private fun formatTime(amount: Long, unit: String): String {
        return if (amount == 1L) "A $unit ago" else "$amount $unit ago"
    }

}

//operator fun Date.minus(other: Date): Int {
//    val diff = this.time - other.time
//    return (diff / (1000 * 60 * 60 * 24)).toInt()
//}