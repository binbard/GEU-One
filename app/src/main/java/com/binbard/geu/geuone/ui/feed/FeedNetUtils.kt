package com.binbard.geu.geuone.ui.feed

import android.util.Log
import com.binbard.geu.geuone.models.StatusCode
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.UnknownHostException

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
            return Pair(StatusCode.UNKNOWN, "")
        }

    }

    fun parseXml(xmlData: String): List<Feed> {
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