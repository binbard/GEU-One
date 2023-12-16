package com.binbard.geu.geuone.ui.erp

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.binbard.geu.geuone.ui.erp.menu.ErpStudentViewModel
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
    fun preLogin(context: Context?) {
        val id = erpCacheHelper.getStudentId()
        val password = erpCacheHelper.getPassword()

        GlobalScope.launch(Dispatchers.IO) {
            val token = ErpNetUtils.getToken(cookies)
            Log.d("ErpRepository", "id: $id, password: $password, token: $token, cookies: $cookies")
            val captchaImg = ErpNetUtils.getCaptcha(cookies)
            if(captchaImg==null){
                withContext(Dispatchers.Main) {
                    Log.d("ErpRepository", "Failed to load captcha")
                }
                return@launch
            }
            val captchaTxt = ocrUtils.getText(captchaImg)
            val loginResponse = ErpNetUtils.login(id, password, token, captchaTxt, cookies)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "$loginResponse", Toast.LENGTH_SHORT).show()
            }

            Log.d("ErpNetUtils", "$loginResponse $id $password $captchaTxt $token\n$cookies")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getStudentDetails(vm: ErpStudentViewModel){
        GlobalScope.launch(Dispatchers.IO) {
            val studentDetails = ErpNetUtils.getStudentDetails(cookies)
            withContext(Dispatchers.Main) {
                if(studentDetails==null){
                    vm.details.value = "Failed to Load Details"
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

}