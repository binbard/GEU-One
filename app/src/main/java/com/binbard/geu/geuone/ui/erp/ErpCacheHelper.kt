package com.binbard.geu.geuone.ui.erp

import android.content.Context

class ErpCacheHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("erp", Context.MODE_PRIVATE)

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
        sharedPreferences.edit().putString("image", value).apply()
    }

    fun getStudentImage(): String {
        return sharedPreferences.getString("image", "") ?: ""
    }

    fun getLoginStatus(): Int {
        return sharedPreferences.getInt("loginStatus", 0)
    }

    fun saveLoginStatus(value: Int) {
        sharedPreferences.edit().putInt("loginStatus", value).apply()
    }

    fun loadLocalData(erpViewModel: ErpViewModel) {
        erpViewModel.erpStudentId.value = getStudentId()
        erpViewModel.erpPassword.value = getPassword()
        erpViewModel.erpStudentName.value = getStudentName()
        erpViewModel.erpStudentImg.value = getStudentImage()
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