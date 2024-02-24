package com.binbard.geu.one.ui.erp

import android.util.Log
import com.binbard.geu.one.models.LoginStatus
import com.binbard.geu.one.utils.BitmapHelper
import kotlinx.coroutines.*
import java.util.*

@OptIn(DelicateCoroutinesApi::class)
class ErpRepository(private val erpCacheHelper: ErpCacheHelper) {
    var cookies: String
    val ocrUtils = OCRUtils()

    init {
        cookies = erpCacheHelper.getCookies()
        if (cookies == "") {
            GlobalScope.launch(Dispatchers.IO) {
                cookies = ErpNetUtils.getCookies()
                erpCacheHelper.saveCookies(cookies)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun preLogin(erpViewModel: ErpViewModel) {
        val id = erpCacheHelper.getStudentId()
        val password = erpCacheHelper.getPassword()

        GlobalScope.launch(Dispatchers.IO) {
            val token = ErpNetUtils.getToken(cookies)
            val captchaImg = ErpNetUtils.getCaptcha(cookies)
            if(captchaImg==null){
                Log.d("ErpRepository", "Failed to load captcha")
                return@launch
            }
            val captchaTxt = ocrUtils.getText(captchaImg)
            val loginResponse = ErpNetUtils.login(id, password, token, captchaTxt, cookies)
            withContext(Dispatchers.Main) {
                Log.d("ErpRepository", "$loginResponse $id $password $captchaTxt $token\n$cookies")

                if(loginResponse=="SUCCESS"){
                    if(erpViewModel.loginStatus.value==LoginStatus.PREV_LOGGED_IN){
                        Log.d("ErpRepository", "Auto Login Successful")
                    } else{
                        Log.d("ErpRepository", "Login Successful")
                    }
                    erpViewModel.loginStatus.value = LoginStatus.LOGIN_SUCCESS
                }
                else if(loginResponse=="INVALID_CAPTCHA"){
                    Log.d("ErpRepository", "Login Failed (Invalid Captcha). Retrying...")
                    preLogin(erpViewModel)
                } else if(loginResponse=="INVALID_CREDENTIALS"){
                    Log.d("ErpRepository", "Login Failed")
                    erpViewModel.loginStatus.value = LoginStatus.LOGIN_FAILED
                } else{
                    Log.d("ErpRepository", "Login Failed")
                    erpViewModel.comments.value = "Something went wrong"
                    erpViewModel.erpCacheHelper?.saveLog(loginResponse)
                    erpViewModel.loginStatus.value = LoginStatus.LOGIN_FAILED
                }
            }

        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchAttendance(erpViewModel: ErpViewModel){
        GlobalScope.launch(Dispatchers.IO) {
            val attendance = erpViewModel.studentData.value?.regID?.let { ErpNetUtils.getAttendance(cookies, it) }
            erpViewModel.attendanceData.postValue(attendance)
            if(attendance!=null){
                erpCacheHelper.saveLocalAttendanceData(attendance)
                Log.d("ErpRepository", "Attendance Synced")
            } else{
                Log.d("ErpRepository", "Failed to Sync Attendance")
                erpViewModel.comments.postValue("Failed to Sync Attendance")
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchImage(erpViewModel: ErpViewModel){
        GlobalScope.launch(Dispatchers.IO) {
            val image = ErpNetUtils.getStudentImage(cookies)
            if(image!=null){
                erpCacheHelper.saveStudentImage(BitmapHelper.bitmapToString(image))
                erpViewModel.erpStudentImg.postValue(BitmapHelper.bitmapToString(image))
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun syncStudentData(erpViewModel: ErpViewModel){
        Log.d("ErpRepository", "ZZZ syncStudentData")
        fetchImage(erpViewModel)
        GlobalScope.launch(Dispatchers.IO) {
            val studentDetails = ErpNetUtils.getStudentDetails(cookies)
            erpViewModel.studentData.postValue(studentDetails)
            if(studentDetails!=null){
                erpCacheHelper.saveStudentId(studentDetails.studentID)

                erpCacheHelper.saveLocalStudentData(studentDetails)
            } else{
                erpViewModel.comments.postValue("Failed to Sync Student Details")
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchMidtermMarks(erpViewModel: ErpViewModel, sem: Int){
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("ErpRepository", "YYY fetchMidtermMarks")
            val midtermMarksData = erpViewModel.studentData.value?.regID?.let { ErpNetUtils.getMidtermMarks(cookies, it, sem) }
            erpViewModel.midtermMarksData.postValue(midtermMarksData)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchExamMarks(erpViewModel: ErpViewModel){
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("ErpRepository", "CCC fetchExamMarks")
            val examMarksData = erpViewModel.studentData.value?.regID?.let { ErpNetUtils.getExamMarks(cookies, it) }
            erpViewModel.examMarksData.postValue(examMarksData)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchFeeDetails(erpViewModel: ErpViewModel, feeType: Int){
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("ErpRepository", "MMM fetchFeeDetails")
            val feeDetails = ErpNetUtils.fetchFeeDetails(cookies, feeType)
            if(feeDetails!=null && feeType==2 && feeDetails.headdatahostel.isEmpty()){
                erpViewModel.feeDetails.postValue(null)
                return@launch
            }
            erpViewModel.feeDetails.postValue(feeDetails)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun resetErpPassword(erpViewModel: ErpViewModel, id: String, email: String, dob: String){
        GlobalScope.launch(Dispatchers.IO) {
            val params = "ID=$id&Mob=$email&db=${dob.replace("/","%2F")}"
            val code = ErpNetUtils.resetErpPassword(cookies, params)
            Log.d("ErpRepository", "DDD resetErpPassword $code")
            withContext(Dispatchers.Main){
                if(code=="NotMatch") erpViewModel.loginStatus.value = LoginStatus.RESET_NOTMATCH
                else erpViewModel.loginStatus.value = LoginStatus.RESET_MATCH
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun changeErpPassword(erpViewModel: ErpViewModel, password: String, url: String){
        GlobalScope.launch(Dispatchers.IO) {
            if(url=="") return@launch
            val newCookies = ErpNetUtils.preChangeErpPassword(cookies, url)
            if(newCookies=="x"){
                withContext(Dispatchers.Main){
                    erpViewModel.loginStatus.value = LoginStatus.CHANGE_PASSWORD_EXPIRED
                }
                return@launch
            }
            erpCacheHelper.saveCookies(newCookies)
            cookies = newCookies
            Log.d("ErpRepository", "EEE preChangeErpPassword $newCookies")
            val result = ErpNetUtils.changeErpPassword(cookies, password)
            // result: "{"data":1}"
            val code = result.substringAfter(":").substringBefore("}")
            Log.d("ErpRepository", "EEE resetErpPassword $result  |  $code")
            withContext(Dispatchers.Main){
                if(code=="1") erpViewModel.loginStatus.value = LoginStatus.CHANGE_PASSWORD_SUCCESS
                else erpViewModel.loginStatus.value = LoginStatus.CHANGE_PASSWORD_FAILED
            }
        }
    }

}