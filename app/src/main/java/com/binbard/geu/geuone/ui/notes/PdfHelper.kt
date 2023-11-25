package com.binbard.geu.geuone.ui.notes

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import java.io.File

object PdfHelper{
    private val downloadingFiles = mutableListOf<String>()
    fun openOrDownloadPdf(context: Context, url: String, fileName: String) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "$fileName.pdf"
        )

        if (file.exists()) {
            openPdf(context,file)
        } else {
            if (downloadingFiles.contains(url)) {
                Toast.makeText(context, "File is already being downloaded", Toast.LENGTH_SHORT).show()
            } else {
                downloadPdf(context,url, "$fileName.pdf")
            }
        }
    }

    private fun openPdf(context: Context,file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No PDF reader found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadPdf(context: Context,url: String, fileName: String) {
        if (hasPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            downloadingFiles.add(url)

        } else {
            requestPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) { granted ->
                if (granted) {
                    downloadPdf(context, url, fileName)
                } else {
                    Toast.makeText(context, "Permission denied. Cannot download PDF.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:${context.packageName}")
                    startActivity(context, intent, null)
                }
            }
        }
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(context: Context, permission: String, callback: (Boolean) -> Unit) {
        val requestCode = 1
        ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), requestCode)
    }

    val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val fileName = intent.getStringExtra(DownloadManager.COLUMN_TITLE)
            fileName?.let {
                downloadingFiles.remove(fileName)
            }
        }
    }
}