package com.binbard.geu.geuone.ui.feed

import java.time.LocalDate

data class Feed(
    val link: String,
    val title: String,
    val date: FDate
){

    class FDate(
        val day: Int,
        val month: Int,
        val year: Int,
        val hour: Int = 0,
        val minute: Int = 0,
        val second: Int = 0
    ) {
        override fun toString(): String {
            return if (hour == 0 && minute == 0 && second == 0)
                "$day/$month/$year"
            else "$day/$month/$year $hour:$minute:$second"
        }

        fun diff(): String {
            val today = LocalDate.now()
            val todayDate = FDate(today.dayOfMonth, today.monthValue, today.year)
            val diff = todayDate - this
            return if (diff == 0)
                "Today"
            else if (diff == 1)
                "Yesterday"
            else if(diff < 7)
                "$diff days ago"
            else if(diff < 30){
                if(diff < 14)
                    "Last week"
                else "${diff / 7} weeks ago"
            }
            else if(diff < 365){
                if(diff < 60)
                    "A month ago"
                else "${diff / 30} months ago"
            }
            else if(diff < 730)
                "A year ago"
            else "${diff / 365} years ago"
        }

        operator fun minus(other: FDate): Int {
            return (this.year - other.year) * 365 + (this.month - other.month) * 30 + (this.day - other.day)
        }
    }
}
