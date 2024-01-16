package com.binbard.geu.geuone.ui.feed

import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.R
import java.io.File
import java.util.*

class FeedRecyclerAdapter() :
    RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder>() {
    private var feeds = mutableListOf<Feed>()
    private val hostUrl = "https://csitgeu.in/wp/"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFeedTitle: TextView = itemView.findViewById(R.id.tvFeedTitle)
        val tvFeedDate: TextView = itemView.findViewById(R.id.tvFeedDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feed = feeds[position]
        holder.tvFeedTitle.text = feed.title
        holder.tvFeedDate.text = feed.date.toString()

        holder.itemView.setOnClickListener {
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(it.context, "$hostUrl${feed.slug}".toUri())
        }
    }

    override fun getItemCount(): Int {
        return feeds.size
    }

    fun addFeeds(feeds: List<Feed>) {
        val startPos = this.feeds.size
        this.feeds.addAll(feeds)
        notifyItemRangeInserted(startPos, feeds.size)
    }
}
