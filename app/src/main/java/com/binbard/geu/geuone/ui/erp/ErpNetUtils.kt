package com.binbard.geu.geuone.ui.erp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.binbard.geu.geuone.ui.erp.ErpNetUtils.client
import com.binbard.geu.geuone.ui.erp.menu.StateData
import com.binbard.geu.geuone.ui.erp.menu.Student
import com.binbard.geu.geuone.utils.BitmapHelper
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
            val cookiesList = response.headers("Set-Cookie")
            val cookieSet = HashSet(cookiesList)
            var cookies = ""
            for(cookie in cookieSet){
                val mainCookie = cookie.split(";")[0]
                cookies += mainCookie + ";"
            }
            cookies
        } catch (e: Exception) {
            Log.d("ErpNetUtils", "Error: ${e.message}")
            ""
        }
    }

    suspend fun getToken(cookies: String): String = withContext(Dispatchers.IO) {
        val request = okhttp3.Request.Builder()
            .url(erpUrl)
            .header("Cookie", cookies)
            .build()
        try {
            val response = client.newCall(request).execute()
            val doc = Jsoup.parse(response.body?.string())
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
            .header("Cookie", cookies)
            .post(formBody)
            .build()

        return@withContext try {
            val response = client.newCall(request).execute().peekBody(Long.MAX_VALUE)
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

        val client1 = client.newBuilder().followRedirects(false).build()

        val request = okhttp3.Request.Builder()
            .url(erpUrl)
            .header("Cookie", cookies)
            .post(fill)
            .build()

        val response = client1.newCall(request).execute()

        if(response.code == 302){
            val cookiesList = response.headers("Set-Cookie")
            if(cookiesList.isEmpty()){
                if(response.body?.string()?.contains("Captcha does not match") == true){
                    return@withContext "INVALID_CAPTCHA"
                } else return@withContext "x"
            }
            val uid = cookiesList[0].split("=")[2].split("&")[0]
            return@withContext uid
        }
        return@withContext "x"

    }



    fun getStudentDetails(cookies: String): Student? {
        val request = okhttp3.Request.Builder()
            .url("$erpUrl/Account/GetStudentDetail")
            .header("Cookie", cookies)
            .post(FormBody.Builder().build())
            .build()
        try {
            val response = client.newCall(request).execute()

            val body = response.body?.string()

            val json = body?.replace("\\", "")?.replace("\"[", "[")?.replace("]\"", "]")

            val stateData: StateData = Gson().fromJson(json, StateData::class.java)
            val details = stateData.state

            if(details.isEmpty()) return null

            var result = ""

            val studentData = details[0]

            for(data in studentData.properties){
                result += "${data.first}: ${data.second}\n"
            }

            return studentData

        } catch (e: Exception) {
            return null
        }
    }

    fun getStudentImage(cookies: String): Bitmap?{
        val request = okhttp3.Request.Builder()
            .url("$erpUrl/Account/show")
            .header("Cookie", cookies)
            .post(FormBody.Builder().build())
            .build()
        try {
            val response = client.newCall(request).execute()
            return BitmapHelper.decodeBase64(response.body?.byteStream()!!)
        } catch (e: Exception) {
            return null
        }
    }

}