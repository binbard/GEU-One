package com.binbard.geu.one.ui.erp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.ActivityScanqrBinding
import com.binbard.geu.one.models.FetchStatus
import com.binbard.geu.one.models.QrScanInput
import com.binbard.geu.one.models.QrScanResult
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
import io.github.g00fy2.quickie.config.ScannerConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class ScanqrActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanqrBinding
    private lateinit var evm: ErpViewModel
    private var job: Job? = null
    private var name: String? = null
    private var uid: String? = null

    val scanCustomCode = registerForActivityResult(ScanCustomCode(), ::handleResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanqrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        name = intent.getStringExtra("name")?.toLowerCase()?.split(" ")
            ?.joinToString(" ") { it.replaceFirstChar { it.uppercase() } }
        uid = intent.getStringExtra("uid")

        binding.pbScanView.isIndeterminate = true

        evm = ViewModelProvider(this)[ErpViewModel::class.java]
        evm.erpRepository = ErpRepository(this, ErpCacheHelper(this))

        binding.imgScanView.visibility = android.view.View.GONE
        binding.tvScanTitle.text = ""
        binding.tvScanMsg.text = "Please wait..."
        binding.tvScanTime.text = ""
        binding.imgScanClock.visibility = android.view.View.GONE
        binding.imgScanWave.visibility = android.view.View.GONE

        var counter = 0
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                binding.tvScanMsg.text = "Please wait${".".repeat(counter)}"
                counter = (counter % 3) + 1
                delay(1000)
            }
        }

        evm.qrScanResult.observe(this) {
            if (it == null) return@observe
            if (it.status == "ERROR") {
                binding.imgScanView.setImageResource(R.drawable.ic_error_warning)
            } else if (it.status == "SUCCESS") {
                binding.imgScanView.setImageResource(R.drawable.ic_tick_square)

                val currentTime = System.currentTimeMillis()
                val time =
                    android.text.format.DateFormat.format("MMM dd, yyyy hh:mm a", currentTime)
                        .toString()
                binding.tvScanTime.text = time
                binding.imgScanClock.visibility = android.view.View.VISIBLE
            }
            job?.cancel()
            binding.pbScanView.visibility = android.view.View.GONE
            binding.tvScanTitle.text = it.title
            binding.tvScanMsg.text = it.msg

            binding.imgScanView.visibility = android.view.View.VISIBLE
            binding.imgScanWave.visibility = android.view.View.VISIBLE
        }

        scanCustomCode.launch(
            ScannerConfig.build {
                setBarcodeFormats(listOf(BarcodeFormat.FORMAT_QR_CODE))
                setOverlayStringRes(R.string.scan_barcode)
                setHapticSuccessFeedback(false)
                setShowTorchToggle(true)
                setShowCloseButton(true)
                setKeepScreenOn(true)
            }
        )
    }

    private fun handleResult(result: QRResult) {
        val qrValue = if (result is QRResult.QRSuccess) result.content.rawValue else null
        if (qrValue == null) {
            evm.qrScanResult.value =
                QrScanResult("ERROR", "Something went wrong", "Please Scan the QR Code again", -1)
            return
        }
        val qrScanInput = parseQrInput(qrValue)
        if (qrScanInput == null) {
            evm.qrScanResult.value =
                QrScanResult("ERROR", "Something went wrong", "Scan a valid QR Code", -1)
            return
        }
        evm.erpRepository?.scanQrCode(
            evm,
            qrScanInput
        )
    }

    private fun parseQrInput(qrValue: String): QrScanInput? {
        if (name == null || uid == null) return null
        val qrData = getMyString("pY7dGk0tWn3rSq5j",qrValue)
        if(qrData == null) return null
        val qrValues = qrData.split(",")
        Log.d("QR", qrValues.toString())
        if (qrValues.size != 3) return null
        val url = qrValues[0]
        val token = qrValues[1]
        val type = qrValues[2]
        return QrScanInput(url, token, name!!, uid!!, type)
    }

    fun getMyString(key: String, enc: String): String? {
        try {
            val mKey = key.toByteArray()
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val edb = Base64.getDecoder().decode(enc)
            val iv = IvParameterSpec(edb.copyOfRange(0, 16))
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(mKey, "AES"), iv)
            val dec = cipher.doFinal(edb.copyOfRange(16, edb.size))
            return String(dec)
        } catch (e: Exception) {
            return null
        }
    }

}