package com.binbard.geu.one.ui.res

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.one.R
import com.binbard.geu.one.helpers.PdfUtils
import com.binbard.geu.one.models.ResObj
import com.bumptech.glide.Glide

class GridAdapter(private val itemList: List<ResObj>) : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {
    val intent = CustomTabsIntent.Builder().build()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_res, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val item = itemList[position]
        if(item.imgUrl != null){
            Glide.with(holder.imgNoteItem.context).load(item.imgUrl).into(holder.imgNoteItem)
        }
        holder.tvNoteItem.text = item.name
        if (item.type == "pdf") {
            holder.mView.setOnClickListener {
                PdfUtils.openOrDownloadPdf(
                    context = holder.tvNoteItem.context,
                    item.url,
                    isExternalSource = true
                )
            }
        } else if (item.type == "link") {
            holder.mView.setOnClickListener {
                intent.launchUrl(it.context, item.url.toUri())
            }
        } else if (item.type == "web") {
            holder.mView.setOnClickListener {
                val intent = Intent(it.context, com.binbard.geu.one.ViewWebActivity::class.java)
                intent.putExtra("url", item.url)
                intent.putExtra("title", item.name)
                intent.putExtra("options", item.options)
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mView: View = itemView
        val imgNoteItem: ImageView = itemView.findViewById(R.id.imgNoteItem)
        val tvNoteItem: TextView = itemView.findViewById(R.id.tvNoteItem)
    }
}
