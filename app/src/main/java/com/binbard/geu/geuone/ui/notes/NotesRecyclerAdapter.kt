package com.binbard.geu.geuone.ui.notes

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.*
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.R
import kotlinx.coroutines.NonCancellable.children
import java.io.File

class NotesRecyclerAdapter(private val context: Context, private val nvm: NotesViewModel): RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNoteItem: TextView = itemView.findViewById(R.id.tvNoteItem)
        val imgNoteItem: ImageView = itemView.findViewById(R.id.imgNoteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_notes, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fsItems = nvm.notes.value!!.children.toList()
        val item = fsItems[position]
        holder.tvNoteItem.text = item.getFileName()

        if(item.isFolder()) {
            holder.imgNoteItem.setImageResource(R.drawable.ic_folder_with_files)
            changeAlpha(holder.imgNoteItem, item.getFileName())
        } else {
            holder.imgNoteItem.setImageResource(R.drawable.ic_pdf_file_2)
            changeAlpha(holder.imgNoteItem, item.getFileName()+".pdf")
        }

        holder.itemView.setOnClickListener {
            if(item.isFolder()) {
                nvm.gotoNextDir(item.name)
            }
            else{
                PdfUtils.openOrDownloadPdf(context,item.url!!, item.getFileName())
                changeAlpha(holder.imgNoteItem, item.getFileName()+".pdf")
            }
        }
    }

    private fun changeAlpha(img: ImageView, fileName: String){
        if(!fileName.endsWith(".pdf")){
            img.alpha = 1f
        }
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/${context.getString(
            R.string.app_name)}/$fileName")
        if (file.exists()) {
            img.alpha = 1f
        } else {
            img.alpha = 0.5f
        }
    }

    override fun getItemCount(): Int{
        return nvm.notes.value!!.children.size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        context.unregisterReceiver(PdfUtils.onComplete)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context.registerReceiver(PdfUtils.onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

}