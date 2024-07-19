package com.binbard.geu.one.helpers

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("geu", Context.MODE_PRIVATE)
    private val sharedPreferencesNotes: SharedPreferences =
        context.getSharedPreferences("notes", Context.MODE_PRIVATE) // TODO: remove this in next version

    fun getInitDone(): Boolean {
        return sharedPreferences.getBoolean("initDone", false)
    }
    fun setInitDone(value: Boolean) {
        sharedPreferences.edit().putBoolean("initDone", value).apply()
    }

    fun getFbToken(): String {
        return sharedPreferences.getString("fbToken", "") ?: ""
    }
    fun setFbToken(value: String) {
        sharedPreferences.edit().putString("fbToken", value).apply()
    }

    fun getInstalledTime(): Long {
        return sharedPreferences.getLong("installedOnTime", 0)
    }

    fun setInstalledTime(value: Long) {
        sharedPreferences.edit().putLong("installedOnTime", value).apply()
    }

    fun getLastSyncTime(): Long {
        return sharedPreferences.getLong("lastSyncTime", 0)
    }
    fun setLastSyncTime(value: Long) {
        sharedPreferences.edit().putLong("lastSyncTime", value).apply()
    }

    fun getCampus(): String {
        return sharedPreferences.getString("campus", "") ?: ""
    }
    fun setCampus(value: String) {
        sharedPreferences.edit().putString("campus", value).apply()
    }
}