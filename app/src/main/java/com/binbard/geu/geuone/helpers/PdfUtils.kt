package com.binbard.geu.geuone.ui.notes

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.io.path.Path

object PdfUtils {
    private const val REQUEST_WRITE_EXTERNAL_STORAGE = 123

    private fun getParentDir(context: Context): File {
        val pdfPath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "GEU One")
        if (!pdfPath.exists()) pdfPath.mkdirs()
        return pdfPath
    }

    fun getFile(context: Context, fileName: String): File {
        return File(getParentDir(context), "$fileName.pdf")
    }

    private fun openPdf(context: Context, file: File){
        Log.d("PdfUtils", "Opening File: ${file.absolutePath}")
        val uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show()
        }
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    private fun downloadPdf(context: Context, url: String, file: File){
        Log.d("PdfUtils", "Will download File: ${file.absolutePath}")
        if (file.exists()) {
            Toast.makeText(context, "File already exists", Toast.LENGTH_LONG).show()
            return
        }
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle(file.name)
        request.setDescription("Downloading ${file.name}...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(context, "GEU One", file.name)
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
        Log.d("PdfUtils", "Downloading File: ${file.absolutePath}")
        Toast.makeText(context, "Downloading file...", Toast.LENGTH_LONG).show()
    }

    fun openOrDownloadPdf(context: Context, url: String, pdfTitle: String) {
        val file = getFile(context, pdfTitle)

        if (hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (file.exists()) {
                openPdf(context, file)
            } else { // File does not exist. Download it
                if (hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    downloadPdf(context, url, file)
                } else {
                    requestPermission(context as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_EXTERNAL_STORAGE)
                }
            }
        } else {
            requestPermission(context as Activity, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_WRITE_EXTERNAL_STORAGE)
        }
    }
}
