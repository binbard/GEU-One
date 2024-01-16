package com.binbard.geu.geuone.ui.feed

import android.text.TextUtils.replace
import android.util.Log
import com.binbard.geu.geuone.models.StatusCode
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

object FeedNetUtils {

    fun makeHttpRequest(url: String): Pair<StatusCode,String> {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        try{
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return Pair(StatusCode.FAILED, "")
            }
            return Pair(StatusCode.SUCCESS, response.body?.string() ?: "")
        } catch (e: UnknownHostException) {
            return Pair(StatusCode.NO_INTERNET, "")
        } catch (e: IOException) {
            return Pair(StatusCode.IO_ERROR, "")
        } catch (e: Exception) {
            return Pair(StatusCode.UNKNOWN_ERROR, "")
        }

    }

    fun parsePostsJson(jsonData: String): List<Feed> {
        val dataList = mutableListOf<Feed>()
        // jsonData: {"status":"ok","count":2,"count_total":773,"pages":387,"posts":[{"id":4257,"slug":"notice-5th-semester-mini-project-external-evaluation-schedule-2","date":"2024-01-10 15:31:25"},{"id":4249,"slug":"debarred-students-time-table-of-b-tech-cse-iiird-vth-semester-2023-24","date":"2024-01-04 20:19:20"}]}
        val posts = jsonData.substringAfter("posts\":[{").substringBefore("}]").split("},{")
        for (post in posts) {
            val id = post.substringAfter("id\":").substringBefore(",").toInt()
            val slug = post.substringAfter("slug\":\"").substringBefore("\"")
            val title = post.substringAfter("title\":\"").substringBefore("\"")
            val date = post.substringAfter("date\":\"").substringBefore("\"")

            val mTitle = title.replace("\\\\u([0-9A-Fa-f]{4})".toRegex()) {     // Decode unicode
                String(Character.toChars(it.groupValues[1].toInt(radix = 16)))
            }
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
            val feedDate = sdf.parse(date) ?: continue

            dataList.add(Feed(id, slug, mTitle, feedDate))
        }

        return dataList
    }
}