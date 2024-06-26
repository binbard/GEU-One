package com.binbard.geu.one.models

import com.google.gson.annotations.SerializedName
import java.util.*


data class PresentAbsent(
    @SerializedName("AttendanceDate") var date: Date,
    @SerializedName("AttendanceType") var present: String,
    @SerializedName("Period") var period: String,
    @SerializedName("InsertDate") var insertDate: String,
)

data class PresentAbsentWrapper(
    @SerializedName("state") var presentAbsent: List<PresentAbsent>
)

data class SubjectAttendance(
    @SerializedName("SubjectID") var subjectId: String,
    @SerializedName("Subject") var subject: String,
    @SerializedName("SubjectCode") var subjectCode: String,
    @SerializedName("PeriodAssignID") var periodAssignId: String,
    @SerializedName("TTID") var ttid: String,
    @SerializedName("LectureTypeID") var lectureTypeId: String,
    @SerializedName("Employee") var employee: String,
    @SerializedName("TotalLecture") var totalLecture: String,
    @SerializedName("TotalPresent") var totalPresent: String,
    @SerializedName("Percentage") var percentage: String,
)

data class TotalAttendance(
    @SerializedName("DateFrom") var dateFrom: Date,
    @SerializedName("DateTo") var dateTo: Date,
    @SerializedName("TotalLecture") var totalLecture: Int,
    @SerializedName("TotalPresent") var totalPresent: Int,
    @SerializedName("TotalLeave") var totalLeave: Int,
    @SerializedName("TotalPercentage") var totalPercentage: Float,
)

data class AttendanceGson(
    @SerializedName("state") var subjectAttendance: List<SubjectAttendance>,
    @SerializedName("data") var totalAttendance: List<TotalAttendance>,
)

data class Attendance(
    var subjectAttendance: List<SubjectAttendance>,
    var totalAttendance: TotalAttendance,
)