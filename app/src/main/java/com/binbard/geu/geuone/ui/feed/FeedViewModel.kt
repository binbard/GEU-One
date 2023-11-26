package com.binbard.geu.geuone.ui.feed

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.UnknownHostException

class FeedViewModel(application: Application): AndroidViewModel(application) {
    private val _text = MutableLiveData<String>().apply {
        value = "The Feed"
    }
    val feedText: LiveData<String> = _text

    var someError = MutableLiveData<String>().apply {
        value = ""
    }

    private val _feedList = MutableLiveData<List<Feed>>()
    val feedList: LiveData<List<Feed>> = _feedList

    private lateinit var repository: FeedRepository

    init{
        val feedDao = AppDatabase.getInstance(application).feedDao()
        repository = FeedRepository(feedDao)

        fetchData()
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchData() {
        Log.d("BIN_X", "FeedViewModel.fetchData: ")
        GlobalScope.launch(Dispatchers.IO) {
            var cachedFeeds = repository.getSomeFeeds()
            if (cachedFeeds.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    _feedList.value = cachedFeeds.map { it.toFeed() }
                }
                cachedFeeds = repository.getAllFeeds()
                withContext(Dispatchers.Main) {
                    _feedList.value = cachedFeeds.map { it.toFeed() }
                }
            }

            val response = makeHttpRequest("https://csitgeu.in/wp/wp-sitemap-posts-post-1.xml")

            if(response.isEmpty()) return@launch
            val feedList = parseXml(response)
            withContext(Dispatchers.Main) {
                _feedList.value = feedList
            }

            repository.insertFeeds(feedList.map { it.toFeedEntity() })
        }
    }
    private fun makeHttpRequest(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        try{
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                someError.postValue("Could not fetch Feeds")
                return ""
            }
            return response.body?.string() ?: ""
        } catch (e: UnknownHostException) {
            someError.postValue("No Internet Connection")
            return ""
        } catch (e: IOException) {
            someError.postValue("IO Error")
            return ""
        } catch (e: Exception) {
            someError.postValue("Something went wrong")
            return ""
        }

    }
    private fun parseXml(xmlData: String): List<Feed> {
        val dataList = mutableListOf<Feed>()

        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xmlData))

        var eventType = parser.eventType
        var feedLink = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "url" -> feedLink = ""
                        "loc" -> feedLink = parser.nextText()
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "url") {
                        val arr = feedLink.split("/")
                        try{
                            val feedDate = Feed.FDate(arr[6].toInt(), arr[5].toInt(), arr[4].toInt())

                            val slug = arr[arr.size-2]
                            val feedTitle = slug.split("-").map { it.capitalize() }.joinToString(" ")

                            dataList.add(0, Feed(feedLink, feedTitle, feedDate))
                        } catch (e: Exception) {
                            Log.e("BIN_X_ERROR", "FeedViewModel.parseXml: $e")
                            break
                        }

                    } else if (parser.name == "urlset") {
                        // End of the document
                    }
                }
            }
            eventType = parser.next()
        }
        return dataList
    }

}