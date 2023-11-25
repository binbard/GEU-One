package com.binbard.geu.geuone.ui.notes

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.R
import java.io.File

class NotesRecyclerAdapter(private val context: Context, private var fsItem: FSItem): RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder>() {
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
        }
        holder.itemView.setOnClickListener {
            if(item.isFolder()) {
                fsItem = item
                notifyDataSetChanged()
            }
            else{
                Toast.makeText(context, "Opening ${item.url}", Toast.LENGTH_SHORT).show()
                openOrDownloadPdf(item.url!!, item.getFileName())
            }
        }
    }

    override fun getItemCount(): Int{
        return fsItem.children.size
    }
    fun openOrDownloadPdf(url: String, fileName: String) {
        val file = File(context.filesDir, fileName)

        if (file.exists()) {
            openPdf(file)
        } else {
            downloadPdf(url, fileName)
        }
    }

    fun openPdf(file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle exception if no PDF reader is installed
            Toast.makeText(context, "No PDF reader found", Toast.LENGTH_SHORT).show()
        }
    }

    fun downloadPdf(url: String, fileName: String) {
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle("PDF Download")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            // Set the destination in the app's private directory
            request.setDestinationInExternalFilesDir(context, null, fileName)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) { granted ->
                if (granted) {
                    // Permission granted, retry the operation
                    downloadPdf(url, fileName)
                } else {
                    // Permission denied, show a message or take appropriate action
                    Toast.makeText(context, "Permission denied. Cannot download PDF.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(permission: String, callback: (Boolean) -> Unit) {
        val requestCode = 1 // You can use a different code
        ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), requestCode)

        // The result will be handled in onRequestPermissionsResult
    }


}