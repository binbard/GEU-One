package com.binbard.geu.geuone.ui.notes

import android.app.Activity
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File

object PdfUtils {
    private val downloadingFiles = mutableListOf<Pair<Long,String>>()

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

    fun downloadThumb(context: Context, url: String, fileName: String){
        val file = getFileThumb(context, fileName)
        if(!file.exists()){
            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle(file.name)
            request.setDescription("Downloading ${file.name}")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationUri(file.toUri())
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }
    }

    private fun openPdf(context: Context, file: File){
        val uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle(file.name)
        request.setDescription("Downloading ${file.name}")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationUri(file.toUri())
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = manager.enqueue(request)
        downloadingFiles.add(Pair(id, file.nameWithoutExtension))
    }

    fun openOrDownloadPdf(context: Context, url: String, saveName: String = "") {
        val fileName = url.substringAfterLast("/").substringBeforeLast(".pdf")
        val pdfTitle = if(saveName == "") fileName else saveName

        val file = getFile(context, pdfTitle)

        if (file.exists()) {
            openPdf(context, file)
        }
        else{
            if(!isDownloading(fileName)){
                downloadPdf(context, url, file)
            } else{
                Toast.makeText(context, "Already downloading...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isDownloading(fileName: String): Boolean {
        return downloadingFiles.indexOfFirst { it.second == fileName } != -1
    }

    fun removeDownloading(id: Long): String? {
        val index = downloadingFiles.indexOfFirst { it.first == id }
        if (index != -1){
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
