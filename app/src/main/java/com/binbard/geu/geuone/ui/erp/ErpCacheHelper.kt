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

    fun saveId(value: String) {
        sharedPreferences.edit().putString("id", value).apply()
    }

    fun getId(): String {
        return sharedPreferences.getString("id", "21021913") ?: ""
    }

    fun savePassword(value: String) {
        sharedPreferences.edit().putString("password", value).apply()
    }

    fun getPassword(): String {
        return sharedPreferences.getString("password", "notok1234") ?: ""
    }
}