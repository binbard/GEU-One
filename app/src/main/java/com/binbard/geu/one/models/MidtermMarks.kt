package com.binbard.geu.one.models

import com.google.gson.annotations.SerializedName

data class MidtermMarksData(
    val state: List<MidtermMarks>,
)

data class MidtermMarks(
    @SerializedName("Marks") val marks: Int,
    @SerializedName("Subject") val subject: String,
    @SerializedName("SubjectId") val subjectId: String,
    @SerializedName("MaxMarks") val maxMarks: Int,
)