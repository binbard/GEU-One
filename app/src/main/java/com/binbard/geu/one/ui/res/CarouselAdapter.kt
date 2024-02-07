package com.binbard.geu.one.ui.res

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.one.R
import com.bumptech.glide.Glide

class CarouselAdapter(private val context: Context, private val images: List<Pair<String,String>>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {
    val intent = CustomTabsIntent.Builder().build()

    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_res_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val (imageUrl,openUrl) = images[position]
        if(openUrl!=""){
            holder.imageView.setOnClickListener {
                intent.launchUrl(context, openUrl.toUri())
            }
        }
        Glide.with(context).load(imageUrl).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return images.size
    }
}
