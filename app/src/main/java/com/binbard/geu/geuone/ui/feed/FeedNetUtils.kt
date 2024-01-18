package com.binbard.geu.geuone.ui.feed

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import com.binbard.geu.geuone.models.FeedPost
import com.binbard.geu.geuone.models.FeedPostWrapper
import com.binbard.geu.geuone.models.FetchStatus
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*

object FeedNetUtils {

    val client = OkHttpClient()
    val builder = Request.Builder()
    fun makeHttpRequest(url: String): Pair<FetchStatus,String> {
        val request = builder.url(url).build()

        try{
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return Pair(FetchStatus.FAILED, "")
            }
            return Pair(FetchStatus.SUCCESS, response.body?.string() ?: "")
        } catch (e: Exception) {
            return Pair(FetchStatus.FAILED, "")
        }

    }

    fun parseDecode(txt: String): String{
        var text = txt.replace("\\\\u([0-9A-Fa-f]{4})".toRegex()) {     // Decode unicode
            String(Character.toChars(it.groupValues[1].toInt(radix = 16)))
        }
        text = text.replace("&#[0-9]+;".toRegex()) {                  // Decode html entities
            String(Character.toChars(it.value.substringAfter("&#").substringBefore(";").toInt()))
        }
        return text
    }

    fun parseFeedListJson(jsonData: String): List<Feed> {
        val dataList = mutableListOf<Feed>()
        // jsonData: {"status":"ok","count":2,"count_total":773,"pages":387,"posts":[{"id":4257,"slug":"notice-5th-semester-mini-project-external-evaluation-schedule-2","date":"2024-01-10 15:31:25"},{"id":4249,"slug":"debarred-students-time-table-of-b-tech-cse-iiird-vth-semester-2023-24","date":"2024-01-04 20:19:20"}]}
        val posts = jsonData.substringAfter("posts\":[{").substringBefore("}]").split("},{")
        for (post in posts) {
            val id = post.substringAfter("id\":").substringBefore(",").toInt()
            val slug = post.substringAfter("slug\":\"").substringBefore("\"")
            val title = post.substringAfter("title\":\"").substringBefore("\"")
            val date = post.substringAfter("date\":\"").substringBefore("\"")

            val feedTitle = parseDecode(title)

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
            val feedDate = sdf.parse(date) ?: continue

            dataList.add(Feed(id, slug, feedTitle, feedDate))
        }

        return dataList
    }

    fun parsePostJson(url: String): FeedPost?{
        val request = builder.url(url).build()

        try{
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return null

            val jsonData = response.body?.string() ?: ""

            val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
            val feedPostWrapper = gson.fromJson(jsonData, FeedPostWrapper::class.java)
            feedPostWrapper.post.title = parseDecode(feedPostWrapper.post.title)
            return feedPostWrapper.post
        } catch (e: Exception) {
            Log.e("FeedNetUtils", "parsePostJson: ${e.message}")
            return null
        }
    }

}