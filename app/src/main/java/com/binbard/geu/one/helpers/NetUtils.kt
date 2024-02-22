package com.binbard.geu.one.helpers

import android.content.Context
import android.util.Log
import com.binbard.geu.one.NanoMessagingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object NetUtils {
    val client = OkHttpClient()

    fun sendFeedback(context: Context, url: String, type: String, msg: String) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {

                val request = Request.Builder()
                    .url(url)
                    .post(
                        FormBody.Builder()
                            .add("type", type)
                            .add("msg", msg)
                            .build()
                    )
                    .build()
                Log.d("NetUtils", "FEEDBACK $request")
                try{
                    val response = client.newCall(request).execute()
                    Log.d("NetUtils", "RESPONSE ${response.body?.string()}")
                    letsDoThis2(context)
                    response.body?.close()
                } catch (e: Exception){
                    Log.d("NetUtils","Failed to give Feedback")
                }
            }
        }
    }

    fun letsDoThis2(context: Context) {
        val nanoMessagingService = NanoMessagingService()
        nanoMessagingService.sendNotification(
            context,
            "Feedback received. Thankyou for your valuable time."
        )
    }
}