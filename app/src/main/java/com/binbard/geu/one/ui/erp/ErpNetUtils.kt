package com.binbard.geu.one.ui.erp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.binbard.geu.one.R
import com.binbard.geu.one.helpers.SharedPreferencesHelper
import com.binbard.geu.one.models.*
import com.binbard.geu.one.ui.erp.menu.Student
import com.binbard.geu.one.ui.erp.menu.StudentGson
import com.binbard.geu.one.utils.BitmapHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup
import java.time.Duration
import java.util.*


class ErpNetUtils(context: Context) {
    private val client = OkHttpClient()
    val sharedPreferencesHelper = SharedPreferencesHelper(context)
    val campus = sharedPreferencesHelper.getCampus()

    private val erpUrl = if (campus == "deemed") {
        context.resources.getString(R.string.erpHostUrlDeemed)
    } else {
        context.resources.getString(R.string.erpHostUrlHill)
    }

    suspend fun getCookies(): String = withContext(Dispatchers.IO) {
        val request = okhttp3.Request.Builder()
            .url(erpUrl)
            .build()
        return@withContext try {
            val response = client.newCall(request).execute()
            val cookiesList = response.headers("Set-Cookie")
            val cookieSet = HashSet(cookiesList)
            var cookies = ""
            for (cookie in cookieSet) {
                val mainCookie = cookie.split(";")[0]
                cookies += "$mainCookie;"
            }
            response.body?.close()
            cookies
        } catch (e: Exception) {
            Log.d("ErpNetUtils", "Error: ${e.message}")
            ""
        }
    }

    fun mergeCookies(xcookies: String, cookiesList: List<String>): String {
        val cookiesMap = HashMap<String, String>()
        val xcookiesList = xcookies.split(";")
        for (cookie in xcookiesList) {
            if (cookie == "") continue
            val key = cookie.split("=")[0]
            val value = cookie.split("=")[1]
            cookiesMap[key] = value
        }
        for (cookie in cookiesList) {
            val key = cookie.split("=")[0]
            val value = cookie.split("=")[1]
            cookiesMap[key] = value
        }
        var cookies = ""
        for ((key, value) in cookiesMap) {
            cookies += "$key=$value;"
        }
        Log.d("ErpNetUtils", "AAA: $cookies")
        return cookies
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
            response.body?.close()
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

    suspend fun login(
        id: String,
        password: String,
        token: String,
        captchaText: String,
        cookies: String
    ): String =
        withContext(Dispatchers.IO) {

            val fill = FormBody.Builder()
                .add("hdnMsg", "GEU")
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

            if (response.code == 302) {
                val cookiesList = response.headers("Set-Cookie")
                val uid = cookiesList[0].split("=")[2].split("&")[0]
                response.body?.close()
                return@withContext "SUCCESS"
            } else {
                val body = response.body?.string() ?: ""
                if (body.contains("Captcha does not match")) {
                    response.body?.close()
                    return@withContext "INVALID_CAPTCHA"
                } else if (body.contains("The user name or password provided is incorrect.")) {
                    response.body?.close()
                    return@withContext "INVALID_CREDENTIALS"
                }
            }
            Log.d("ErpNetUtils", "got login code: ${response.code}")
            val cookiesList = response.headers("Set-Cookie")
            Log.d("ErpNetUtils", "got login cookies: $cookiesList")
            response.body?.close()
            return@withContext "xAB"

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

            val studentGson: StudentGson = Gson().fromJson(json, StudentGson::class.java)
            val details = studentGson.state

            if (details.isEmpty()) {
                response.body?.close()
                return null
            }

            var result = ""

            val studentData = details[0]

            for (data in studentData.properties) {
                result += "${data.first}: ${data.second}\n"
            }

            response.body?.close()
            return studentData

        } catch (e: Exception) {
            return null
        }
    }

    fun getStudentImage(cookies: String): Bitmap? {
        val request = okhttp3.Request.Builder()
            .url("$erpUrl/Account/show")
            .header("Cookie", cookies)
            .post(FormBody.Builder().build())
            .build()
        try {
            val response = client.newCall(request).execute()
            val img = BitmapHelper.decodeBase64(response.body?.byteStream()!!)
            response.body?.close()
            return img
        } catch (e: Exception) {
            return null
        }
    }

    fun getAttendance(cookies: String, regID: String): Attendance? {
        val request = okhttp3.Request.Builder()
            .url("$erpUrl/Web_StudentAcademic/GetSubjectDetailStudentAcademicFromLive")
            .header("Cookie", cookies)
            .post(FormBody.Builder().add("RegID", regID).build())
            .build()
        return try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            val json = body?.replace("\\", "")?.replace("\"[", "[")?.replace("]\"", "]")
            val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
            val attendanceGson = gson.fromJson(json, AttendanceGson::class.java)
            val attendance =
                Attendance(attendanceGson.subjectAttendance, attendanceGson.totalAttendance[0])
            response.body?.close()
            attendance
        } catch (e: Exception) {
            null
        }
    }

    fun getSubjectAttendance(
        cookies: String,
        regID: String,
        subjectID: String,
        periodAssignID: String,
        ttid: String,
        lectureTypeID: String,
        dateFrom: String,
        dateTo: String
    ): PresentAbsentWrapper? {
        val request = okhttp3.Request.Builder()
            .url("$erpUrl/Web_StudentAcademic/FillAttendanceDetail")
            .header("Cookie", cookies)
            .post(FormBody.Builder()
                .add("RegID", regID)
                .add("SubjectID", subjectID)
                .add("PeriodAssignID", periodAssignID)
                .add("TTID", ttid)
                .add("LectureTypeID", lectureTypeID)
                .add("DateFrom", dateFrom)
                .add("DateTo", dateTo)
                .build())
            .build()
        return try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            val json = body?.replace("\\", "")?.replace("\"[", "[")?.replace("]\"", "]")
            val gson: Gson = GsonBuilder().setDateFormat("dd/MM/yyyy").create()
            val presentAbsentWrapper = gson.fromJson(json, PresentAbsentWrapper::class.java)
            response.body?.close()
            presentAbsentWrapper
        } catch (e: Exception) {
            Log.d("ErpNetUtils", "getSubjectAttendance: ${e.message}")
            null
        }
    }

    fun getMidtermMarks(cookies: String, regID: String, sem: Int): MidtermMarksData? {
        val request = okhttp3.Request.Builder()
            .url("$erpUrl/Web_StudentAcademic/GetStudentMidMarks")
            .header("Cookie", cookies)
            .post(FormBody.Builder().add("YearSem", sem.toString()).add("RegID", regID).build())
            .build()
        return try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            val json = body?.replace("\\", "")?.replace("\"[", "[")?.replace("]\"", "]")
            val midtermMarksData = Gson().fromJson(json, MidtermMarksData::class.java)
            response.body?.close()
            midtermMarksData
        } catch (e: Exception) {
            null
        }
    }

    fun getExamMarks(cookies: String, regID: String): ExamMarksData? {
        val request = okhttp3.Request.Builder()
            .url("$erpUrl/Web_StudentAcademic/GetStudentExamSummary")
            .header("Cookie", cookies)
            .post(FormBody.Builder().add("RegID", regID).build())
            .build()
        return try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            val json = body?.replace("\\", "")?.replace("\"[", "[")?.replace("]\"", "]")
            val examMarksData = Gson().fromJson(json, ExamMarksData::class.java)
            response.body?.close()
            examMarksData
        } catch (e: Exception) {
            null
        }
    }

    fun fetchFeeDetails(cookies: String, feeType: Int, duration: Int = 0): FeeDetails? {
        val request = okhttp3.Request.Builder()
            .url("$erpUrl/Web_StudentFinance/FillHead")
            .header("Cookie", cookies)
            .post(
                FormBody.Builder().add("FeeType", feeType.toString())
                    .add("duration", duration.toString()).build()
            )
            .build()

        return try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            val json = body?.replace("\\", "")?.replace("\"[", "[")?.replace("]\"", "]")
            val feeDetails = Gson().fromJson(json, FeeDetails::class.java)
            response.body?.close()
            feeDetails
        } catch (e: Exception) {
            null
        }
    }

    fun updateChannel(url: String, json: String): String {
        val request = okhttp3.Request.Builder()
            .url(url)
            .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
        return try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()
            response.body?.close()
            body ?: ""
        } catch (e: Exception) {
            return "x"
        }
    }

    fun resetErpPassword(cookies: String, params: String): String {
        val request = okhttp3.Request.Builder()
            .url("$erpUrl/Account/ResetPassword?$params")
            .header("Cookie", cookies)
            .build()
        return try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()
            response.body?.close()
            body ?: ""
        } catch (e: Exception) {
            return ""
        }
    }

    fun preChangeErpPassword(cookies: String, link: String): String {
        val request = okhttp3.Request.Builder()
            .method("GET", null)
            .url(link)
            .header("Cookie", cookies)
            .build()
        return try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()
//            Log.d("ErpNetUtils", "preChangeErpPassword: $body")
            val cookiesList = response.headers("Set-Cookie")
            val newCookies = mergeCookies(cookies, cookiesList)
            response.body?.close()
            if (body != null && body.contains("This Reset Password Link is expired")) return "x"
            newCookies
        } catch (e: Exception) {
            return ""
        }
    }

    fun changeErpPassword(cookies: String, password: String): String {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val json = "{Password: '$password'}".toRequestBody(mediaType)
        val request = okhttp3.Request.Builder()
            .url("$erpUrl/Account/ChangeUserPassword")
            .header("Cookie", cookies)
            .post(json)
            .build()
        return try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()
            response.body?.close()
            body ?: ""
        } catch (e: Exception) {
            return ""
        }
    }

    fun scanQrCode(qrScanInput: QrScanInput): String {
        val formBody = FormBody.Builder()
            .add("token", qrScanInput.token)
            .add("name", qrScanInput.name)
            .add("uid", qrScanInput.uid)
            .add("type", qrScanInput.type)
            .build()
        val request = okhttp3.Request.Builder()
            .url(qrScanInput.url)
            .post(formBody)
            .build()
        return try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()
            response.body?.close()
            body ?: ""
        } catch (e: Exception) {
            return ""
        }
    }

}