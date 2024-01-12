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

class FeedRecyclerAdapter(private val feeds: List<Feed>) :
    RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder>(), Filterable {

    private val filteredFeeds = feeds.toMutableList()

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
        val feed = filteredFeeds[position]
        holder.tvFeedTitle.text = feed.title
        holder.tvFeedDate.text = feed.date.diff()

        holder.itemView.setOnClickListener {
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(it.context, feed.link.toUri())
        }
    }

    override fun getItemCount(): Int {
        return filteredFeeds.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Feed>()
                if (constraint == null || constraint.isEmpty()) {
                    filteredList.addAll(feeds)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    for (feed in feeds) {
                        if (feed.title.lowercase().contains(filterPattern)) {
                            filteredList.add(feed)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredFeeds.clear()
                filteredFeeds.addAll(results?.values as MutableList<Feed>)
                notifyDataSetChanged()
            }
        }
    }
}
