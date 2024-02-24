package com.binbard.geu.one.ui.feed

import android.util.Log
import com.binbard.geu.one.models.*
import com.binbard.geu.one.ui.feed.FeedNetUtils.builder
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

    fun parseFeedListJson(jsonData: String, campus: String): List<Feed> {
        val dataList = mutableListOf<Feed>()

        val posts: List<FeedPost>
        if(campus=="deemed"){
            val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
            val feedPostMultiWrapper = gson.fromJson(jsonData, FeedPostMultiWrapperDeemed::class.java)
            posts = feedPostMultiWrapper.posts
        } else{
            val gson: Gson = GsonBuilder().create()
            val feedPostMultiWrapperHill = gson.fromJson(jsonData, Array<FeedPostHill>::class.java)
            val feedPostMulti = feedPostMultiWrapperHill.map { it.toFeedPost() }
            posts = feedPostMulti.toList()
        }

        for (post in posts){
            dataList.add(Feed(post.id, post.slug, post.title, post.date))
        }

        return dataList
    }

    fun parsePostJson(url: String, campus: String): FeedPost?{              // Single Posts
        val request = builder.url(url).build()

        try{
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return null

            val jsonData = response.body?.string() ?: ""

            if(campus=="deemed"){
                val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                val feedPostWrapper = gson.fromJson(jsonData, FeedPostWrapperDeemed::class.java)
                return feedPostWrapper.post
            } else{
                val gson: Gson = GsonBuilder().create()
                val feedPostWrapper = gson.fromJson(jsonData, Array<FeedPostHill>::class.java)
                return feedPostWrapper[0].toFeedPost()
            }


        } catch (e: Exception) {
            Log.e("FeedNetUtils", "parsePostJson: ${e.message}")
            return null
        }
    }

}