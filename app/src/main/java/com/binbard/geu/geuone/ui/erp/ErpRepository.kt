package com.binbard.geu.geuone.ui.erp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build.VERSION_CODES.S
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.ui.erp.ErpNetUtils.client
import com.google.gson.Gson
import kotlinx.coroutines.*
import okio.ByteString.Companion.toByteString
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
    fun preLogin(
        etShow: EditText,
        etShow2: EditText,
        etShow3: EditText,
        etShow4: EditText,
        imageView: ImageView,
        textView: TextView
    ) {
        val id = erpCacheHelper.getId()
        val password = erpCacheHelper.getPassword()

        GlobalScope.launch(Dispatchers.IO) {
            val token = ErpNetUtils.getToken(cookies)
            Log.d("ErpRepository", "id: $id, password: $password, token: $token, cookies: $cookies")
            val captchaImg = ErpNetUtils.getCaptcha(cookies)
            if(captchaImg==null){
                withContext(Dispatchers.Main) {
                    Toast.makeText(imageView.context, "Failed to Load Captcha", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            val captchaTxt = ocrUtils.getText(captchaImg)
//            val loginResponse = ErpNetUtils.login(id, password, token, captchaTxt, cookies)

            withContext(Dispatchers.Main) {
                etShow.setText(captchaTxt)
                etShow2.setText("$id $password")
                etShow3.setText(token)
                etShow4.setText(cookies)
//                textView.text = loginResponse
                imageView.setImageBitmap(captchaImg)
            }

            Log.d("ErpNetUtils", "$id $password $captchaTxt $token\n$cookies")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun login(etShow: EditText, etShow2: EditText, etShow3: EditText, textView: TextView){
//        val id = erpCacheHelper.getId()
//        val password = erpCacheHelper.getPassword()
        val idpass = etShow2.text.toString().split(" ")
        if(idpass.size!=2){
            Toast.makeText(etShow.context, "..", Toast.LENGTH_SHORT).show()
            return
        }
        val id = idpass[0]
        val password = idpass[1]
        val token = etShow3.text.toString()
        val captchaTxt = etShow.text.toString()
        GlobalScope.launch(Dispatchers.IO) {
            val loginResponse = ErpNetUtils.login(id, password, token, captchaTxt, cookies)
            withContext(Dispatchers.Main) {
                textView.text = loginResponse
            }
        }
    }

}