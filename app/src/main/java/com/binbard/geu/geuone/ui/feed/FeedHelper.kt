package com.binbard.geu.geuone.ui.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.binbard.geu.geuone.models.StatusCode
import kotlinx.coroutines.*

class FeedHelper(application: Application, private val fvm: FeedViewModel): AndroidViewModel(application) {
    private var repository: FeedRepository

    init{
        val feedDao = AppDatabase.getInstance(application).feedDao()
        repository = FeedRepository(feedDao)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchData(fetchRemote: Boolean) {
        GlobalScope.launch(Dispatchers.IO) {
            var cachedFeeds = repository.getSomeFeeds()
            if (cachedFeeds.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    fvm.feedList.value = cachedFeeds.map { it.toFeed() }
                }
//                cachedFeeds = repository.getAllFeeds()
//                withContext(Dispatchers.Main) {
//                    fvm.feedList.value = cachedFeeds.map { it.toFeed() }
//                }
            }

            if(!fetchRemote){
                withContext(Dispatchers.Main) {
                    fvm.fetchStatus.value = StatusCode.SUCCESS
                }
                return@launch
            }

            val response = FeedNetUtils.makeHttpRequest("https://csitgeu.in/wp/wp-sitemap-posts-post-1.xml")
            val status = response.first
            val data = response.second

            withContext(Dispatchers.Main) {
                fvm.fetchStatus.value = status
            }
            if (status != StatusCode.SUCCESS) return@launch

            if(data.isEmpty()) return@launch
            val feedList = FeedNetUtils.parseXml(data)
            withContext(Dispatchers.Main) {
                fvm.feedList.value = feedList
            }

            repository.insertFeeds(feedList.map { it.toFeedEntity() })
        }
    }

}