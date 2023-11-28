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
    fun login(
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
            val captcha = ErpNetUtils.getCaptcha(token)
            val captchaText = ocrUtils.getText(captcha!!)
            val loginResponse = ErpNetUtils.login(id, password, token, captchaText, cookies)

            withContext(Dispatchers.Main) {
                etShow.setText(captchaText)
                etShow3.setText(token)
                etShow4.setText(cookies)
                textView.text = loginResponse
                imageView.setImageBitmap(captcha)
            }
        }
    }

}