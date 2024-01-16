package com.binbard.geu.geuone.ui.feed

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.binbard.geu.geuone.models.StatusCode
import kotlinx.coroutines.*

class FeedHelper(private val fvm: FeedViewModel){
    private val hostUrl = "https://csitgeu.in/wp/"

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            var cachedFeeds = fvm.feedRepository?.getSomeFeeds()
            if (cachedFeeds?.isNotEmpty() == true) {
                withContext(Dispatchers.Main) {
                    fvm.feedList.value = cachedFeeds?.map { it.toFeed() }
                }
            }

            var response =
                FeedNetUtils.makeHttpRequest("$hostUrl?json=posts&exclude=attachments,author,categories,comment_count,comment_status,comments,content,custom_fields,date,excerpt,modified,slug,status,tags,title,title_plain,type,url&count=1")
            val (status1, data1) = response
            // response:    {"status":"ok","count":1,"count_total":773,"pages":773,"posts":[{"id":4257}]}
            val id = data1.substringAfter("id\":").substringBefore("}")
            if(status1 != StatusCode.SUCCESS || id==fvm.feedRepository?.getLatestPostId().toString()){
                cachedFeeds = fvm.feedRepository?.getAllFeeds()
                withContext(Dispatchers.Main) {
                    fvm.fetchStatus.value = status1
                    fvm.feedList.value = cachedFeeds?.map { it.toFeed() }
                }
                return@launch
            }

            response =
                FeedNetUtils.makeHttpRequest("$hostUrl?json=posts&count=-1&exclude=attachments,author,categories,comment_count,comment_status,comments,content,custom_fields,excerpt,modified,status,tags,title_plain,type,url")
            val (status, data) = response

            withContext(Dispatchers.Main) {
                fvm.fetchStatus.value = status
            }
            if (status != StatusCode.SUCCESS) return@launch

            if (data.isEmpty()) return@launch
            val feedList = FeedNetUtils.parsePostsJson(data)
            withContext(Dispatchers.Main) {
                fvm.feedList.value = feedList
            }

            val feedEntityList = feedList.map { it.toFeedEntity() }
            fvm.feedRepository?.insertFeeds(feedEntityList)
        }
    }

}