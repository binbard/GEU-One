package com.binbard.geu.geuone.ui.erp.menu

import com.google.gson.annotations.SerializedName

data class Student(
    @SerializedName("RegID") var regID: String,
    @SerializedName("StudentID") var studentID: String,
    @SerializedName("EnrollmentNo") var enrollmentNo: String,
    @SerializedName("StudentName") var studentName: String,
    @SerializedName("FatherHusName") var fatherHusName: String,
    @SerializedName("MotherName") var motherName: String,
    @SerializedName("College") var college: String,
    @SerializedName("Course") var course: String,
    @SerializedName("CourseSpecialization") var courseSpecialization: String,
    @SerializedName("University") var university: String,
    @SerializedName("DOB") var dob: String,
    @SerializedName("YearSem") var yearSem: String,
    @SerializedName("Gender") var gender: String,
    @SerializedName("CourseType") var courseType: String,
    @SerializedName("Branch") var branch: String,
    @SerializedName("Section") var section: String,
    @SerializedName("Email") var email: String,
    @SerializedName("MobileNO") var mobileNO: String,
    @SerializedName("FMobileNo") var fMobileNo: String,
    @SerializedName("MRollNo") var mRollNo: String,
    @SerializedName("10") var marks10: String,
    @SerializedName("10+2") var marks12: String,
    @SerializedName("MarksGraduation") var marksGraduation: String,
    @SerializedName("PRollNo") var pRollNo: String,
    @SerializedName("RegisDate") var regisDate: String,
    @SerializedName("PAddress") var pAddress: String,
    @SerializedName("Hostel") var hostel: String,
    @SerializedName("BloodGroup") var bloodGroup: String,
    @SerializedName("OfficialMailID") var officialMailID: String,
    @SerializedName("ClassRollNo") var classRollNo: String,
    @SerializedName("Batch") var batch: String,
    @SerializedName("ABCAccountNo") var abcAccountNo: String,
){
    val properties: List<Pair<String,String>> get() = listOf(
        "Father Name" to fatherHusName,
        "Mother Name" to motherName,
        "D.O.B." to dob,
        "College" to college,
        "Course" to course,
        "Specialization" to courseSpecialization,
        "Year/Sem" to yearSem,
        "Branch" to branch,
        "Section" to section,
        "Class Roll No." to classRollNo,
        "Enroll No." to enrollmentNo,
        "University Roll No." to pRollNo,
        "HighSchool %" to marks10,
        "Intermediate %" to marks12,
    )
}

data class StateData(@SerializedName("state") val state: List<Student>)
