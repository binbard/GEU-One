package com.binbard.geu.one.models

import com.google.gson.annotations.SerializedName

data class ExamMarksData(
    val ExamSummary: List<ExamMarks>,
)

data class ExamMarks(
    @SerializedName("YearSem") val yearSem: String,
    @SerializedName("percnt") val sgpa: String,
    @SerializedName("TotalBack") val totalBack: String,
    @SerializedName("Result") val result: String,
    @SerializedName("Marks") val marks: String,
    @SerializedName("TotalSubject") val totalSubject: String,
)