package com.binbard.geu.geuone.ui.feed

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.binbard.geu.geuone.models.FetchStatus
import com.binbard.geu.geuone.models.StatusCode
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

object FeedHelper{
    private val hostUrl = "https://csitgeu.in/wp/"

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchData(fvm: FeedViewModel) {
        GlobalScope.launch(Dispatchers.IO) {
            val latestFeedDate = fvm.feedRepository?.getLatestFeedDate()
            val sdf = SimpleDateFormat("yyyyMMdd", Locale.ROOT)
            val latestDate = latestFeedDate?.let { sdf.format(it) }

            val response =
                FeedNetUtils.makeHttpRequest("$hostUrl?json=get_posts&count=-1&exclude=attachments,author,categories,comment_count,comment_status,comments,content,custom_fields,excerpt,modified,status,tags,title_plain,type,url&date_query[0][after]=$latestDate")
            val (status, data) = response

            if (status != FetchStatus.SUCCESS || data.isEmpty()) {
                withContext(Dispatchers.Main) {
                    fvm.fetchStatus.value = FetchStatus.FAILED
                }
                return@launch
            }

            val feedList = FeedNetUtils.parsePostsJson(data)

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

}