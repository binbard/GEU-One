package com.binbard.geu.one.helpers

import android.content.Context

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("geu", Context.MODE_PRIVATE)

    fun getInitDone(): Boolean {
        return sharedPreferences.getBoolean("initDone", false)
    }
    fun setInitDone(value: Boolean) {
        sharedPreferences.edit().putBoolean("initDone", value).apply()
    }

    fun getCampus(): String {
        return sharedPreferences.getString("campus", "") ?: ""
    }
    fun setCampus(value: String) {
        sharedPreferences.edit().putString("campus", value).apply()
    }
}