package com.binbard.geu.one.helpers

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.binbard.geu.one.NanoMessagingService
import com.binbard.geu.one.R
import com.binbard.geu.one.models.AppUpdate
import com.binbard.geu.one.ui.erp.ErpViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Integer.max

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
                    val message = "Thankyou for your valuable time."
                    sentNotification(context, message, "Feedback sent")
                    response.body?.close()
                } catch (e: Exception) {
                    val message = "Failed to send Feedback. Please try again later."
                    Log.d("NetUtils", message)
                    sentNotification(context, message, "Error")
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
                    val message = "Thankyou for your valuable time."
                    sentNotification(context, message, "Resource sent for review")
                    response.body?.close()
                } catch (e: Exception) {
                    val message = "Failed to send Resource. Please try again later."
                    Log.d("NetUtils", message)
                    sentNotification(context, message, "Error")
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
                    val message = "Thankyou for your valuable time."
                    sentNotification(context, message, "Notes sent for review")
                    response.body?.close()
                } catch (e: Exception) {
                    val message = "Please try again later."
                    Log.d("NetUtils", message)
                    sentNotification(context, message, "Failed to send Notes")
                }
            }
        }
    }

    fun sentNotification(context: Context, message: String, title: String) {
        val nanoMessagingService = NanoMessagingService()
        nanoMessagingService.sendNotification(
            context,
            message,
            title
        )
    }

    fun getAppUpdateInfo(context: Context, versionCode: Int, evm: ErpViewModel) {
        val updateUrl = context.resources.getString(R.string.updateUrl)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url(updateUrl)
                    .build()
                try {
                    val response = client.newCall(request).execute()
                    val body = response.body?.string()
                    if (body != null) {
                        val gson = Gson().fromJson(body, AppUpdate::class.java)
                        var hp = -1
                        if (gson.p0 != null) for (vc in gson.p0) if (vc.toInt() > versionCode) hp =
                            max(hp, 0)
                        if (gson.p1 != null) for (vc in gson.p1) if (vc.toInt() > versionCode) hp =
                            max(hp, 1)
                        if (gson.p2 != null) for (vc in gson.p2) if (vc.toInt() > versionCode) hp =
                            max(hp, 2)
                        if (gson.p3 != null) for (vc in gson.p3) if (vc.toInt() > versionCode) hp =
                            max(hp, 3)
                        if (gson.p4 != null) for (vc in gson.p4) if (vc.toInt() > versionCode) hp =
                            max(hp, 4)
                        if (gson.p5 != null) for (vc in gson.p5) if (vc.toInt() > versionCode) hp =
                            max(hp, 5)

                        if (hp >= 0) evm.updateAvailable.postValue(hp)
                    }
                    response.body?.close()
                } catch (e: Exception) {
                    val message = "Failed to check for updates. Please try again later."
                    Log.d("NetUtils", message)
                    sentNotification(context, message, "Error")
                }
            }
        }
    }
}