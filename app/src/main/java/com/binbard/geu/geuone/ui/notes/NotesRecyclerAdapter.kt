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
import java.nio.file.Files.exists

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
        val fileName = item.getFileName()
        holder.tvNoteItem.text = fileName

        if(item.isFolder()) {
            holder.imgNoteItem.setImageResource(R.drawable.ic_folder_with_files)
        } else {
            holder.imgNoteItem.setImageResource(R.drawable.ic_pdf_file_2)
        }
        changeAlpha(holder.imgNoteItem, item)

        holder.itemView.setOnClickListener {
            if(item.isFolder()) {
                nvm.gotoNextDir(item.name)
            }
            else{
                PdfUtils.openOrDownloadPdf(context,item.url!!, fileName)
                changeAlpha(holder.imgNoteItem, item)
            }
        }
    }

    private fun changeAlpha(img: ImageView, item: FSItem){
        if(item.isFolder() || PdfUtils.getFile(context, item.getFileName()).exists()) img.alpha = 1.0f
        else img.alpha = 0.5f
    }

    override fun getItemCount(): Int{
        return nvm.notes.value!!.children.size
    }

    fun updateItem(fileName: String){
        val ind = nvm.notes.value!!.getChildIndex(fileName)
        if(ind != -1) notifyItemChanged(ind)
    }

}