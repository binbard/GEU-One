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
                try {
                    val response = client.newCall(request).execute()
                    Log.d("NetUtils", "RESPONSE ${response.body?.string()}")
                    val message = "Feedback received. Thankyou for your valuable time."
                    sentNotification(context, message)
                    response.body?.close()
                } catch (e: Exception) {
                    val message = "Failed to send Feedback. Please try again later."
                    Log.d("NetUtils", message)
                    sentNotification(context, message)
                }
            }
        }
    }

    fun sendResource(context: Context, url: String, msg: String) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {

                val request = Request.Builder()
                    .url(url)
                    .post(
                        FormBody.Builder()
                            .add("type", "RESOURCE")
                            .add("msg", msg)
                            .build()
                    )
                    .build()
                try {
                    val response = client.newCall(request).execute()
                    Log.d("NetUtils", "RESPONSE ${response.body?.string()}")
                    val message = "Resource sent for review. Thankyou for your valuable time."
                    sentNotification(context, message)
                    response.body?.close()
                } catch (e: Exception) {
                    val message = "Failed to send Resource. Please try again later."
                    Log.d("NetUtils", message)
                    sentNotification(context, message)
                }
            }
        }
    }

    fun sendNotes(context: Context, url: String, msg: String) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {

                val request = Request.Builder()
                    .url(url)
                    .post(
                        FormBody.Builder()
                            .add("type", "NOTES")
                            .add("msg", msg)
                            .build()
                    )
                    .build()
                try {
                    val response = client.newCall(request).execute()
                    Log.d("NetUtils", "RESPONSE ${response.body?.string()}")
                    val message = "Notes sent for review. Thankyou for your valuable time."
                    sentNotification(context, message)
                    response.body?.close()
                } catch (e: Exception) {
                    val message = "Failed to send Notes. Please try again later."
                    Log.d("NetUtils", message)
                    sentNotification(context, message)
                }
            }
        }
    }

    fun sentNotification(context: Context, message: String) {
        val nanoMessagingService = NanoMessagingService()
        nanoMessagingService.sendNotification(
            context,
            message
        )
    }
}