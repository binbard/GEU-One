package com.binbard.geu.one.ui.res

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.binbard.geu.one.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResRepository(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("res", Context.MODE_PRIVATE)
    private val resHostUrl = context.getString(R.string.resHostUrl)

    private fun saveResourcesJson(jsonData: String){
        sharedPreferences.edit().putString("resources", jsonData).apply()
    }

    private fun getResourcesJson(): String? {
        return sharedPreferences.getString("resources", null)
    }

    fun fetchResources(rvm: ResViewModel){
        GlobalScope.launch {
            val jsonDataLocal = getResourcesJson() ?: return@launch
            val resListLocal = ResNetUtils.jsonToResList(jsonDataLocal) ?: return@launch
            withContext(rvm.viewModelScope.coroutineContext) {
                rvm.resList.value = resListLocal
            }

            val jsonData = ResNetUtils.fetchResources(resHostUrl) ?: return@launch
            val resList = ResNetUtils.jsonToResList(jsonData) ?: return@launch
            saveResourcesJson(jsonData)
            withContext(rvm.viewModelScope.coroutineContext) {
                rvm.resList.value = resList
            }
        }
    }
}