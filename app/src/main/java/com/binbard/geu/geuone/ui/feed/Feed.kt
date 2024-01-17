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
        val diffInMinutes = (Date().time - date.time) / (1000 * 60)
        val diffInHours = diffInMinutes / 60
        val diffInDays = diffInHours / 24
        val diffInWeeks = diffInDays / 7
        val diffInMonths = diffInDays / 30
        val diffInYears = diffInDays / 365

        if(diffInYears > 0){
            if(diffInYears == 1L) return "A year ago"
            return "$diffInYears years ago"
        }
        if(diffInMonths > 0){
            if(diffInMonths == 1L) return "A month ago"
            return "$diffInMonths months ago"
        }
        if(diffInWeeks > 0){
            if(diffInWeeks == 1L) return "A week ago"
            return "$diffInWeeks weeks ago"
        }
        if(diffInDays > 0){
            if(diffInDays == 1L) return "Yesterday"
            return "$diffInDays days ago"
        }
        if(diffInHours > 0){
            if(diffInHours == 1L) return "An hour ago"
            if(diffInHours <= 12) return "$diffInHours hours ago"
            return "Today"
        }
        if(diffInMinutes > 1) return "$diffInMinutes minutes ago"
        return "Just now"
    }
}

//operator fun Date.minus(other: Date): Int {
//    val diff = this.time - other.time
//    return (diff / (1000 * 60 * 60 * 24)).toInt()
//}