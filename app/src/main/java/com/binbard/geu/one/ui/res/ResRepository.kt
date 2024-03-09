package com.binbard.geu.one.ui.res

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.binbard.geu.one.R
import com.binbard.geu.one.helpers.SharedPreferencesHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResRepository(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("res", Context.MODE_PRIVATE)
    private val sharedPreferencesHelper: SharedPreferencesHelper by lazy {
        SharedPreferencesHelper(context)
    }
    private val resHostUrlDeemed = context.getString(R.string.resHostUrlDeemed)
    private val resHostUrlHill = context.getString(R.string.resHostUrlHill)

    private fun saveResourcesJson(jsonData: String){
        sharedPreferences.edit().putString("resources", jsonData).apply()
    }

    private fun getResourcesJson(): String? {
        return sharedPreferences.getString("resources", null)
    }

    fun fetchResources(rvm: ResViewModel){
        GlobalScope.launch {
            val jsonDataLocal = getResourcesJson()
            val resListLocal = jsonDataLocal?.let { ResNetUtils.jsonToResList(it) }
            if(resListLocal!=null) withContext(rvm.viewModelScope.coroutineContext) {
                rvm.resList.value = resListLocal
            }

            val campus = sharedPreferencesHelper.getCampus()
            val resHostUrl = if(campus == "deemed") resHostUrlDeemed else resHostUrlHill

            val jsonData = ResNetUtils.fetchResources(resHostUrl) ?: return@launch
            val resList = ResNetUtils.jsonToResList(jsonData) ?: return@launch
            saveResourcesJson(jsonData)
            withContext(rvm.viewModelScope.coroutineContext) {
                rvm.resList.value = resList
            }
        }
    }
}