package com.binbard.geu.one.ui.feed

import android.util.Log
import com.binbard.geu.one.models.*
import com.binbard.geu.one.ui.feed.FeedNetUtils.builder
import com.binbard.geu.one.ui.feed.FeedNetUtils.parseDecode
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

    fun parseFeedListJson(jsonData: String, campus: String): List<Feed> {
        val dataList = mutableListOf<Feed>()

        val gson: Gson = GsonBuilder().create()
        val posts: List<FeedPost>
        if(campus=="deemed"){
            val feedPostMultiWrapper = gson.fromJson(jsonData, FeedPostMultiWrapperDeemed::class.java)
            posts = feedPostMultiWrapper.posts
        } else{
            val feedPostMultiWrapperHill = gson.fromJson(jsonData, Array<FeedPostHill>::class.java)
            val feedPostMulti = feedPostMultiWrapperHill.map { it.toFeedPost() }
            posts = feedPostMulti.toList()
        }

        for (post in posts){
            dataList.add(Feed(post.id, post.slug, parseDecode(post.title), post.modified))
        }

        return dataList
    }

    fun parsePostJson(url: String, campus: String): FeedPost?{              // Single Posts
        val request = builder.url(url).build()

        try{
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return null

            val jsonData = response.body?.string() ?: ""

            val gson: Gson = GsonBuilder().create()

            if(campus=="deemed"){
                val feedPostWrapper = gson.fromJson(jsonData, FeedPostWrapperDeemed::class.java)
                feedPostWrapper.post.title = parseDecode(feedPostWrapper.post.title)
                return feedPostWrapper.post
            } else{
                val feedPostWrapper = gson.fromJson(jsonData, Array<FeedPostHill>::class.java)
                return feedPostWrapper[0].toFeedPost()
            }


        } catch (e: Exception) {
            Log.e("FeedNetUtils", "parsePostJson: ${e.message}")
            return null
        }
    }

}