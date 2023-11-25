package com.binbard.geu.geuone.ui.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.R

class NotesRecyclerAdapter(private var fsItem: FSItem): RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder>() {
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
        }
    }

    override fun getItemCount(): Int{
        return fsItem.children.size
    }
}