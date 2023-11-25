package com.binbard.geu.geuone.ui.notes

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.R
import java.io.File

class NotesRecyclerAdapter(private val context: Context, private var fsItem: FSItem): RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder>() {

    private val downloadingFiles = mutableListOf<String>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNoteItem: TextView = itemView.findViewById(R.id.tvNoteItem)
        val imgNoteItem: ImageView = itemView.findViewById(R.id.imgNoteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_notes, parent, false)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fsItems = fsItem.children.toList()
        val item = fsItems[position]
        holder.tvNoteItem.text = item.getFileName()
        if(item.isFolder()) {
            holder.imgNoteItem.setImageResource(R.drawable.ic_folder_with_files)
        } else {
            holder.imgNoteItem.setImageResource(R.drawable.ic_pdf_file_2)
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), item.getFileName()+".pdf")
            if (file.exists()) {
                holder.imgNoteItem.alpha = 1f
            } else {
                holder.imgNoteItem.alpha = 0.5f
            }
        }
        holder.itemView.setOnClickListener {
            if(item.isFolder()) {
                fsItem = item
                notifyDataSetChanged()
            }
            else{
                openOrDownloadPdf(item.url!!, item.getFileName())
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), item.getFileName()+".pdf")
                if (file.exists()) {
                    holder.imgNoteItem.alpha = 1f
                } else {
                    holder.imgNoteItem.alpha = 0.5f
                }
            }
        }
    }

    override fun getItemCount(): Int{
        return fsItem.children.size
    }
    private fun openOrDownloadPdf(url: String, fileName: String) {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "$fileName.pdf"
        )

        if (file.exists()) {
            openPdf(file)
        } else {
            if (downloadingFiles.contains(url)) {
                Toast.makeText(context, "File is already being downloaded", Toast.LENGTH_SHORT).show()
            } else {
                downloadPdf(url, "$fileName.pdf")
            }
        }
    }

    private fun openPdf(file: File) {
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

    private fun downloadPdf(url: String, fileName: String) {
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            downloadingFiles.add(url)

        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) { granted ->
                if (granted) {
                    downloadPdf(url, fileName)
                } else {
                    Toast.makeText(context, "Permission denied. Cannot download PDF.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:${context.packageName}")
                    startActivity(context, intent, null)
                }
            }
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String, callback: (Boolean) -> Unit) {
        val requestCode = 1
        ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), requestCode)
    }

    private val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val fileName = intent.getStringExtra(DownloadManager.COLUMN_TITLE)
            fileName?.let {
                downloadingFiles.remove(fileName)
            }
        }
    }

    fun registerDownloadReceiver() {
        context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }
    fun unregisterDownloadReceiver() {
        context.unregisterReceiver(onComplete)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        unregisterDownloadReceiver()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        registerDownloadReceiver()
    }



}