package com.binbard.geu.geuone.ui.erp

import android.content.Context
import com.binbard.geu.geuone.models.LoginStatus
import com.binbard.geu.geuone.ui.erp.menu.Student
import com.google.gson.Gson

class ErpCacheHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("erp", Context.MODE_PRIVATE)
    private val spStudentData = context.getSharedPreferences("student_data", Context.MODE_PRIVATE)
    private val spStudentImg = context.getSharedPreferences("student_image", Context.MODE_PRIVATE)

    fun saveCookies(value: String) {
        sharedPreferences.edit().putString("cookies", value).apply()
    }

    fun getCookies(): String {
        return sharedPreferences.getString("cookies", "") ?: ""
    }

    fun saveStudentId(value: String) {
        sharedPreferences.edit().putString("id", value).apply()
    }

    fun getStudentId(): String {
        return sharedPreferences.getString("id", "") ?: ""
    }

    fun savePassword(value: String) {
        sharedPreferences.edit().putString("password", value).apply()
    }

    fun getPassword(): String {
        return sharedPreferences.getString("password", "") ?: ""
    }

    fun saveStudentUid(value: String) {
        sharedPreferences.edit().putString("uid", value).apply()
    }

    fun getStudentUid(): String {
        return sharedPreferences.getString("uid", "") ?: ""
    }

    fun saveStudentName(value: String) {
        sharedPreferences.edit().putString("name", value).apply()
    }

    fun getStudentName(): String {
        return sharedPreferences.getString("name", "") ?: ""
    }

    fun saveStudentImage(value: String) {
        spStudentImg.edit().putString("image", value).apply()
    }

    fun getStudentImage(): String {
        return spStudentImg.getString("image", "") ?: ""
    }

    fun saveLoginStatus(loginStatus: LoginStatus) {
        sharedPreferences.edit().putInt("loginStatus", loginStatus.ordinal).apply()
    }

    fun getLoginStatus(): LoginStatus {
        val statusOrdinal = sharedPreferences.getInt("loginStatus", LoginStatus.PREV_LOGGED_OUT.ordinal)
        return LoginStatus.values()[statusOrdinal]
    }


    fun loadLocalStudentData(erpViewModel: ErpViewModel) {
        erpViewModel.erpStudentId.value = getStudentId()
        erpViewModel.erpPassword.value = getPassword()
        erpViewModel.erpStudentName.value = getStudentName()
        erpViewModel.erpStudentImg.value = getStudentImage()

        val studentJson = spStudentData.getString("studentData", "")
        if(studentJson!=""){
            val student = Gson().fromJson(studentJson, Student::class.java)
            erpViewModel.studentData.value = student
        }
    }

    fun saveLocalStudentData(student: Student) {
        val studentJson = Gson().toJson(student)
        spStudentData.edit().putString("studentData", studentJson).apply()
    }

    fun clearLocalData(){
//        sharedPreferences.edit().clear().apply()
        sharedPreferences.edit().remove("id").apply()
        sharedPreferences.edit().remove("password").apply()
        sharedPreferences.edit().remove("uid").apply()
        sharedPreferences.edit().remove("name").apply()
        sharedPreferences.edit().remove("image").apply()
        sharedPreferences.edit().remove("loginStatus").apply()
    }

}