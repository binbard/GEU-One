package com.binbard.geu.one.ui.notes

import android.content.Context
import android.content.SharedPreferences

class NotesCacheHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("notes", Context.MODE_PRIVATE)

    fun saveData(value: String) {
        sharedPreferences.edit().putString("notesList", value).apply()
    }

    fun getData(): String {
        return sharedPreferences.getString("notesList", "") ?: ""
    }

    fun getLastPath(): String {
        return sharedPreferences.getString("lastPath", "Notes") ?: ""
    }

    fun setLastPath(path: String) {
        sharedPreferences.edit().putString("lastPath", path).apply()
    }
}