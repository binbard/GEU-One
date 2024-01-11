package com.binbard.geu.geuone.models

import com.google.gson.annotations.SerializedName

data class MidtermMarks (
    @SerializedName("Subject") var subject: String = "",
    @SerializedName("SubjectCode") var subjectCode: String = "",
    @SerializedName("Marks") var midterm: String = "",
    @SerializedName("MaxMarks") var maxMarks: String = "",
)