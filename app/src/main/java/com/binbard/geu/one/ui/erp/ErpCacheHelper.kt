package com.binbard.geu.one.ui.erp

import android.content.Context
import android.util.Log
import com.binbard.geu.one.models.Attendance
import com.binbard.geu.one.models.LoginStatus
import com.binbard.geu.one.ui.erp.menu.Student
import com.google.gson.Gson

class ErpCacheHelper(context: Context) {
    private val spErp = context.getSharedPreferences("erp", Context.MODE_PRIVATE)
    private val spStudentData = context.getSharedPreferences("student_data", Context.MODE_PRIVATE)
    private val spStudentImg = context.getSharedPreferences("student_image", Context.MODE_PRIVATE)
    private val spAttendance = context.getSharedPreferences("attendance", Context.MODE_PRIVATE)
    private val spLog = context.getSharedPreferences("log", Context.MODE_PRIVATE)

    fun getCookies(): String {
        return spErp.getString("cookies", "") ?: ""
    }
    fun saveCookies(value: String) {
        spErp.edit().putString("cookies", value).apply()
    }

    fun getStudentId(): String {
        return spErp.getString("id", "") ?: ""
    }
    fun saveStudentId(value: String) {
        spErp.edit().putString("id", value).apply()
    }

    fun getPassword(): String {
        return spErp.getString("password", "") ?: ""
    }
    fun savePassword(value: String) {
        spErp.edit().putString("password", value).apply()
    }

    private fun getStudentImage(): String {
        return spStudentImg.getString("image", "") ?: ""
    }
    fun saveStudentImage(value: String) {
        spStudentImg.edit().putString("image", value).apply()
    }

    fun getLoginStatus(): LoginStatus {
        val statusOrdinal = spErp.getInt("loginStatus", LoginStatus.PREV_LOGGED_OUT.ordinal)
        return LoginStatus.values()[statusOrdinal]
    }
    fun saveLoginStatus(loginStatus: LoginStatus) {
        spErp.edit().putInt("loginStatus", loginStatus.ordinal).apply()
    }

    fun saveLocalStudentData(student: Student) {
        val studentJson = Gson().toJson(student)
        spStudentData.edit().putString("studentData", studentJson).apply()
    }
    fun loadLocalStudentData(erpViewModel: ErpViewModel) {
        Log.d("ErpCacheHelper", "ZZZ loadLocalStudentData")
        erpViewModel.erpStudentImg.value = getStudentImage()

        val studentJson = spStudentData.getString("studentData", "")
        if(studentJson!=""){
            val student = Gson().fromJson(studentJson, Student::class.java)
            erpViewModel.studentData.value = student
        }
    }

    fun saveLocalAttendanceData(attendance: Attendance) {
        val attendanceJson = Gson().toJson(attendance)
        spStudentData.edit().putString("attendanceData", attendanceJson).apply()
    }

    fun saveLog(log: String){
        spLog.edit().putString("log", log).apply()
    }

    fun clearLocalData(){
        spErp.edit().clear().apply()
    }

}