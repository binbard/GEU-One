package com.binbard.geu.geuone.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.R

class FeedRecyclerAdapter(private val feeds: List<Feed>): RecyclerView.Adapter<FeedRecyclerAdapter.FeedViewHolder>() {
    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFeedTitle: TextView = itemView.findViewById(R.id.tvFeedTitle)
        val tvFeedDate: TextView = itemView.findViewById(R.id.tvFeedDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feed = feeds[position]
        holder.tvFeedTitle.text = feed.link
        holder.tvFeedDate.text = feed.date
    }

    override fun getItemCount(): Int {
        return feeds.size
    }
}