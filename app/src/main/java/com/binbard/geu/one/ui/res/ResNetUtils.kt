package com.binbard.geu.one.ui.res

import android.util.Log
import com.binbard.geu.one.models.ResSection
import com.binbard.geu.one.ui.feed.FeedNetUtils.builder
import com.google.gson.Gson
import okhttp3.OkHttpClient

object ResNetUtils {
    private val client = OkHttpClient()

    fun fetchResources(url: String): String?{
        val request = builder.url(url).build()
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return null
            }
            return response.body?.string() ?: ""
        } catch (e: Exception) {
            Log.e("ResNetUtils", "fetchResources: ", e)
            return null
        }
    }

    fun jsonToResList(jsonString: String): List<ResSection>? {
        val gson = Gson()
        return try{
            gson.fromJson(jsonString, Array<ResSection>::class.java).toList()
        } catch (e: Exception){
            null
        }
    }
}