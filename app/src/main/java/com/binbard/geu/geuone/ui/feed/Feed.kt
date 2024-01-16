package com.binbard.geu.geuone.ui.feed

import java.time.LocalDate
import java.util.*

data class Feed(
    val id: Int,
    val slug: String,
    val title: String,
    val date: Date
){
    fun getDiff(): String{
        val diff = Date() - this.date
        return if (diff == 0)
            "Today"
        else if (diff == 1)
            "Yesterday"
        else if (diff < 7)
            "$diff days ago"
        else if (diff < 30) {
            if (diff < 14)
                "Last week"
            else "${diff / 7} weeks ago"
        } else if (diff < 365) {
            if (diff < 60)
                "A month ago"
            else "${diff / 30} months ago"
        } else if (diff < 730)
            "A year ago"
        else "${diff / 365} years ago"
    }
}

operator fun Date.minus(other: Date): Int {
    val diff = this.time - other.time
    return (diff / (1000 * 60 * 60 * 24)).toInt()
}