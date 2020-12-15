package com.orange.volunteers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orange.domain.model.HomeRecyclerItem
import kotlinx.android.synthetic.main.home_recycler_view_item.view.*

class MainAdapter (
    private val context: Context,
    private val items: List<HomeRecyclerItem>
) : RecyclerView.Adapter<HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val homeItemView = layoutInflater.inflate(
            R.layout.home_recycler_view_item,
            parent,
            false
        )
        return HomeViewHolder(homeItemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.home_title_tv.text = item.title
        holder.itemView.home_subtitle_tv.text = item.subtitle
        Glide.with(context)
            .load(item.image)
//            .placeholder(R.drawable.ic_view_pager_main)
            .into(holder.itemView.home_image_iv)
    }
}

class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)