package com.binbard.geu.one.ui.notes

import android.content.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.one.R

class NotesRecyclerAdapter(private val context: Context, private val nvm: NotesViewModel) :
    RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.card_notes_item)
        val tvNoteItem: TextView = itemView.findViewById(R.id.tvNoteItem)
        val imgNoteItem: ImageView = itemView.findViewById(R.id.imgNoteItem)
        val imgNoteDownload: ImageView = itemView.findViewById(R.id.imgNoteDownload)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_notes, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fsItems = nvm.notes.value!!.children.toList()
        val item = fsItems[position]
        holder.tvNoteItem.text = item.getFileDisplayName()

        if (item.isFolder()) {
            holder.imgNoteItem.setImageResource(R.drawable.ic_folder_with_files)
        } else {
            showThumbnail(holder.imgNoteItem, item)
        }
        changeAlpha(holder.card, holder.imgNoteItem, holder.imgNoteDownload, item)

        holder.itemView.setOnClickListener {
            if (item.isFolder()) {
                nvm.gotoNextDir(item.name)
            } else {
                val exist =
                    PdfUtils.openOrDownloadPdf(context, item.url!!, item.getFileDisplayName())
                if (!exist) {
                    holder.imgNoteItem.alpha = 0.2f
                    holder.imgNoteDownload.visibility = View.GONE
                }
            }
        }
    }

    private fun showThumbnail(img: ImageView, item: FSItem) {
        val thumbFile = PdfUtils.downloadThumb(
            context,
            item.url!!,
            item.getFileDisplayName(),
            nvm.thumbDownloaded
        )
        if (thumbFile != null) {
            img.setImageURI(thumbFile.toUri())
        } else img.setImageResource(R.drawable.ic_pdf_file_2)
    }

    private fun changeAlpha(
        card: CardView,
        imgNoteItem: ImageView,
        imgNoteDownload: ImageView,
        item: FSItem
    ) {
        if (item.isFolder()) {
            card.cardBackgroundColor.withAlpha(0)
            card.background.alpha = 0
        } else {
            card.cardBackgroundColor.withAlpha(0x33)
            card.background.alpha = 0x33
        }
        if (item.isFolder() || PdfUtils.getFile(context, item.getFileDisplayName()).exists()) {
            imgNoteItem.alpha = 1f
            imgNoteDownload.visibility = View.GONE
        } else {
            imgNoteDownload.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return nvm.notes.value!!.children.size
    }

    fun updateItem(fileName: String) {
        val ind = nvm.notes.value!!.getChildFileIndex(fileName)
        if (ind != -1) notifyItemChanged(ind)
    }

}