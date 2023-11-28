package com.binbard.geu.geuone.ui.erp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import java.util.*

object ErpNetUtils {
    val client = OkHttpClient()

    private const val erpUrl = "https://erp.geu.ac.in"

    suspend fun getCookies(): String = withContext(Dispatchers.IO) {
        val request = okhttp3.Request.Builder()
            .url(erpUrl)
            .build()
        return@withContext try {
            val response = client.newCall(request).execute()
            val cookies = response.headers("Set-Cookie")
            cookies.joinToString("; ")
        } catch (e: Exception) {
            Log.d("ErpNetUtils", "Error: ${e.message}")
            ""
        }
    }

    suspend fun getToken(cookies: String): String = withContext(Dispatchers.IO) {
        try {
            val doc = Jsoup.connect(erpUrl)
                .header("Cookie", cookies)
                .get()
            val token =
                doc.select("input[name=__RequestVerificationToken]").first()?.attr("value") ?: ""
            return@withContext token
        } catch (e: Exception) {
            return@withContext ""
        }
    }

    suspend fun getCaptcha(cookies: String): Bitmap? = withContext(Dispatchers.IO) {
        val formBody = FormBody.Builder()
            .build()
        val request = okhttp3.Request.Builder()
            .url("$erpUrl/Account/showrefreshcaptchaImage")
            .post(formBody)
            .header("Cookie", cookies)
            .build()

        return@withContext try {
            val response = client.newCall(request).execute().peekBody(Long.MAX_VALUE)
            Log.d("ErpNetUtils", "Response: $response")
            val captchaJson = response.string()

            val integerArray: Array<Int> = Gson().fromJson(captchaJson, Array<Int>::class.java)
            val byteArray = integerArray.map { it.toByte() }.toByteArray()
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            Log.d("ErpNetUtils", "Error: ${e.message}")
            null
        }
    }

    suspend fun login(id: String, password: String, token: String, captchaText: String, cookies: String): String =
        withContext(Dispatchers.IO) {

            val fill = FormBody.Builder()
                .add("hdnMsg","GEU")
                .add("checkOnline", "0")
                .add("__RequestVerificationToken", token)
                .add("UserName", id)
                .add("Password", password)
                .add("clientIP", "")
                .add("captcha", captchaText)
                .build()

            val request = okhttp3.Request.Builder()
                .url(erpUrl)
                .header("Cookie", cookies)
                .post(fill)
                .build()

            val response = client.newCall(request).execute()

            return@withContext response.body?.string() ?: ""
        }

}