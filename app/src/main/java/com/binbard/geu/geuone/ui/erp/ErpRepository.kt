package com.binbard.geu.geuone.ui.erp

import android.util.Log
import com.binbard.geu.geuone.utils.BitmapHelper
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
                if(loginResponse=="x"){
                    erpViewModel.loginStatus.value = 0
                    Log.d("ErpRepository", "Login Failed")
                }
                else{
                    erpViewModel.loginStatus.value = 1
                    Log.d("ErpRepository", "Login Successful")
                }
            }

            Log.d("ErpNetUtils", "$loginResponse $id $password $captchaTxt $token\n$cookies")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getStudentDetails(vm: ErpViewModel){
        GlobalScope.launch(Dispatchers.IO) {
            val studentDetails = ErpNetUtils.getStudentDetails(cookies)
            withContext(Dispatchers.Main) {
                if(studentDetails==null){
                    vm.comments.value = "Failed to Load Details"
                    return@withContext
                }
                vm.studentData.value = studentDetails
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun fetchImage(erpCacheHelper: ErpCacheHelper){
        GlobalScope.launch(Dispatchers.IO) {
            val image = ErpNetUtils.getStudentImage(cookies)
            if(image!=null){
                erpCacheHelper.saveStudentImage(BitmapHelper.bitmapToString(image))
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun syncStudentData(erpCacheHelper: ErpCacheHelper){
        fetchImage(erpCacheHelper)
        GlobalScope.launch(Dispatchers.IO) {
            val studentDetails = ErpNetUtils.getStudentDetails(cookies)
            if(studentDetails!=null){
                erpCacheHelper.saveStudentName(studentDetails.studentName)
                erpCacheHelper.saveStudentId(studentDetails.studentID)
            }
        }
    }

}