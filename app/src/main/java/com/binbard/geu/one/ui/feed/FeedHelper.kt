package com.binbard.geu.one.ui.feed

import android.content.Context
import android.util.Log
import com.binbard.geu.one.R
import com.binbard.geu.one.models.FeedPost
import com.binbard.geu.one.models.FetchStatus
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class FeedHelper(context: Context){
    private var feedsHostDeemed = context.resources.getString(R.string.feedsHostDeemed)
    private var feedsHostHill = context.resources.getString(R.string.feedsHostHill)
    private val sp = context.getSharedPreferences("feeds", Context.MODE_PRIVATE)

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchData(fvm: FeedViewModel, campus: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val latestFeedDate = fvm.feedRepository?.getLatestFeedDate()
            val latestDateIso8601 = latestFeedDate?.let { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.ROOT).format(it) }
            val sdf = SimpleDateFormat("yyyyMMdd", Locale.ROOT)
            val latestDate = latestFeedDate?.let { sdf.format(it) }

            val url = if(campus=="deemed"){
                "$feedsHostDeemed?json=get_posts&count=-1&exclude=attachments,author,categories,comment_count,comment_status,comments,content,custom_fields,excerpt,modified,status,tags,title_plain,type,url&date_query[0][after]=$latestDate"
            } else{
                "${feedsHostHill}wp-json/wp/v2/posts?per_page=100&after=$latestDateIso8601"
            }

            val response =
                FeedNetUtils.makeHttpRequest(url)
            val (status, data) = response

            if (status != FetchStatus.SUCCESS || data.isEmpty()) {
                withContext(Dispatchers.Main) {
                    fvm.fetchStatus.value = FetchStatus.FAILED
                }
                return@launch
            }

            val feedList = FeedNetUtils.parseFeedListJson(data,campus)

            val gotNewFeeds = feedList.isNotEmpty() && feedList[0].date != latestFeedDate
            if(!gotNewFeeds){
                withContext(Dispatchers.Main) {
                    fvm.fetchStatus.value = FetchStatus.NO_NEW_DATA_FOUND
                }
                return@launch
            }

            fvm.feedRepository?.insertFeeds(feedList)

            withContext(Dispatchers.Main) {
                fvm.fetchStatus.value = FetchStatus.NEW_DATA_FOUND
            }
        }
    }

    fun fetchFeed(feedUrl: String,campus: String): FeedPost?{
        val feedPost = FeedNetUtils.parsePostJson(feedUrl,campus)
        return feedPost
    }
}