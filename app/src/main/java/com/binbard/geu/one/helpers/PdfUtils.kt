package com.binbard.geu.one.helpers

import android.app.Activity
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.binbard.geu.one.R
import okhttp3.FormBody
import okhttp3.OkHttpClient
import java.io.File

object PdfUtils {
    private val downloadingFiles = mutableListOf<Pair<Long, String>>()
    private val client = OkHttpClient()
    private val builder = okhttp3.Request.Builder()


    private fun getParentDir(context: Context): File {
        val pdfPath = File(context.getExternalFilesDir(null), "pdf_files")
        if (!pdfPath.exists()) pdfPath.mkdirs()
        return pdfPath
    }

    fun getFile(context: Context, fileName: String): File {
        return File(getParentDir(context), "$fileName.pdf")
    }

    fun getFileThumb(context: Context, fileName: String): File {
        return File(getParentDir(context), "$fileName.pdf.jpg")
    }

    fun downloadThumb(
        context: Context, url: String, saveName: String, thumbDownloaded: MutableLiveData<String>
    ): File? {
        val thumbUrl = url.replace(".pdf", ".pdf.jpg")
        val thumbName = saveName.replace(".pdf", ".pdf.jpg")
        val file = getFileThumb(context, thumbName)
        if (!file.exists()) {
            val request = builder.url(thumbUrl).build()
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    Log.e("PdfUtils", "Failed to download thumb for $file.name")
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val bytes = response.body?.bytes()
                    if (bytes != null && bytes.isNotEmpty()) {
                        file.writeBytes(bytes)
                        thumbDownloaded.postValue(saveName)
                    }
                }
            })
            return null
        }
        return file
    }

    private fun openPdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context, context.applicationContext.packageName + ".provider", file
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    private fun downloadPdf(
        context: Context,
        url: String,
        file: File,
        showToastMsg: Boolean = false
    ) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle(file.name)
        request.setDescription("Downloading ${file.name}")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationUri(file.toUri())
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = manager.enqueue(request)
        if (showToastMsg) Toast.makeText(context, "Downloading ${file.name}", Toast.LENGTH_SHORT)
            .show() else
            downloadingFiles.add(Pair(id, file.nameWithoutExtension))
    }

    fun openDownloadFileWithCookies(
        context: Context,
        url: String,
        saveName: String = "",
        cookies: String = ""
    ) {
        var file = File("")
        if (saveName != "") {
            file = getFile(context, saveName)
            if (file.exists()) {
                openPdf(context, file)
                return
            }
        }

        val request = builder.url(url)
            .method("GET", null)
            .addHeader("Cookie", cookies)
        Log.d("PdfUtils", "Downloading File: $url")

        client.newCall(request.build()).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                Log.e("PdfUtils", "Failed to download ${file.name}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val bytes = response.body?.bytes()
                if (bytes != null && bytes.isNotEmpty()) {
                    file.writeBytes(bytes)
                    openPdf(context, file)
                }
            }
        })

    }

    fun downloadOpenIdCard(
        context: Context,
        regId: String,
        saveName: String = "",
        cookies: String = ""
    ) {
        if (saveName != "") {
            val file = getFile(context, saveName)
            if (file.exists()) {
                openPdf(context, file)
                return
            }
        }

        val requesIdInitUrl =
            "${context.resources.getString(R.string.erpHostUrlDeemed)}Account/StudentIDCardPrint"

        val formBody = FormBody.Builder()
            .add("RegID", regId)
        val request = builder.url(requesIdInitUrl)
            .post(formBody.build())
            .addHeader("Cookie", cookies)
        Log.d("PdfUtils", "Init Request: $requesIdInitUrl")

        client.newCall(request.build()).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                Log.e("PdfUtils", "Failed to download IDCard")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val reportRequestUrl =
                    "${context.resources.getString(R.string.erpHostUrlDeemed)}Reports/SMSReportViewer.aspx"

                val request = builder.url(reportRequestUrl)
                    .get()
                    .addHeader("Cookie", cookies)
                Log.d("PdfUtils", "Requesting Report: $reportRequestUrl")

                client.newCall(request.build()).enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                        Log.e("PdfUtils", "Failed to download ${saveName}")
                        Toast.makeText(context, "Failed to download ID Card", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        val body = response.body?.string()
                        val regexPattern =
                            """"(/Reserved\.ReportViewerWebControl\.axd[^"]*?OpType=Export[^"]*)"""".toRegex()
                        val match = regexPattern.find(body ?: "")?.value?.replace("\\u0026", "&")
                        if (match == null) {
                            Log.d("PdfUtils", "Could not get report data")
                            return
                        }
                        val idCardUrl =
                            "${context.resources.getString(R.string.erpHostUrlDeemed)}${match.substring(2, match.length - 1)}PDF"
                        Log.d("PdfUtils", "Report Data: X${idCardUrl}X")
                        openDownloadFileWithCookies(context, idCardUrl, saveName, cookies)
                    }
                })
            }
        })
    }

    fun openFollowDownloadPdf(
        context: Context,
        url: String,
        payload: Map<String, String>,
        cookies: String = "",
        saveName: String = ""
    ) {
        var file = File("")
        if (saveName != "") {
            file = getFile(context, saveName)
            if (file.exists()) {
                openPdf(context, file)
                return
            }
        }
        val baseUrl = url.substringBeforeLast("/")

        val formBody = FormBody.Builder()
        for ((key, value) in payload) {
            formBody.add(key, value)
        }
        val request0 = builder.url(url).post(formBody.build())
        request0.addHeader("Cookie", cookies)

        client.newCall(request0.build()).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                Log.e("PdfUtils", "Failed to follow file")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string()
                // {"msg":"OK","data":1,"docNo":"2205048_4"}
                val docNo = body?.substringAfter("docNo\":\"")?.substringBefore("\"") ?: ""
                if (saveName == "") file = getFile(context, docNo)
                if (file.exists()) {
                    openPdf(context, file)
                    return
                }

                if (docNo == "") {
                    Log.e("PdfUtils", "Failed to download ${file.name}")
                    return
                }

                val request = builder.url("$baseUrl/DownloadFile?docNo=$docNo").method("GET", null)
                request.addHeader("Cookie", cookies)
                Log.d("PdfUtils", "Downloading $baseUrl/DownloadFile?docNo=$docNo")

                client.newCall(request.build()).enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                        Log.e("PdfUtils", "Failed to download ${file.name}")
                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        val bytes = response.body?.bytes()
                        if (bytes != null && bytes.isNotEmpty()) {
                            file.writeBytes(bytes)
                            openPdf(context, file)
                        }
                    }
                })
            }
        })

    }


    fun openOrDownloadPdf(
        context: Context,
        url: String,
        saveName: String = "",
        cookies: String = "",
        isExternalSource: Boolean = false
    ): Boolean {
        val fileName = url.substringAfterLast("/").substringBeforeLast(".pdf").replace("%20", " ")
        val pdfTitle = if (saveName == "") fileName else saveName

        val file = getFile(context, pdfTitle)

        return if (file.exists()) {
            openPdf(context, file)
            true
        } else {
            if (!isDownloading(pdfTitle)) {
                downloadPdf(context, url, file)
            } else {
                Toast.makeText(context, "Already downloading...", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }

    private fun isDownloading(fileName: String): Boolean {
        return downloadingFiles.indexOfFirst { it.second == fileName } != -1
    }

    fun removeDownloading(id: Long): String? {
        val index = downloadingFiles.indexOfFirst { it.first == id }
        if (index != -1) {
            val fileName = downloadingFiles[index].second
            downloadingFiles.removeAt(index)
            return fileName
        }
        return null
    }

    fun clearAllFiles(context: Context) {
        val dir = getParentDir(context)
        dir.listFiles()?.forEach {
            it.delete()
        }
    }

}
