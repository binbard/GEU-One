package com.binbard.geu.geuone.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class FeedViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "The Feed"
    }
    val feedText: LiveData<String> = _text

    private val _feedList = MutableLiveData<List<Feed>>()
    val feedList: LiveData<List<Feed>> = _feedList

    init{
        fetchData()
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = makeHttpRequest("https://csitgeu.in/wp/wp-sitemap-posts-post-1.xml")
            val feedList = parseXml(response)
            withContext(Dispatchers.Main) {
                _feedList.value = feedList
            }
        }
    }
    private fun makeHttpRequest(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            return response.body?.string() ?: ""
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
                            val feedDate = FDate(arr[6].toInt(), arr[5].toInt(), arr[4].toInt())

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