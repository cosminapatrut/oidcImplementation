package com.orange.volunteers.campaigns.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orange.domain.model.CampaignPicturesModel
import com.orange.volunteers.R
import kotlinx.android.synthetic.main.campaigns_images_rv_item.view.*

class CampaignImagesAdapter(
    private val context: Context,
    private val items: List<CampaignPicturesModel>
) : RecyclerView.Adapter<CampaignImagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignImagesViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val homeItemView = layoutInflater.inflate(
            R.layout.campaigns_images_rv_item,
            parent,
            false
        )
        return CampaignImagesViewHolder(
            homeItemView
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CampaignImagesViewHolder, position: Int) {
        val item = items[position]
        Glide.with(context)
            .load(item.uri)
//            .placeholder(R.drawable.ic_view_pager_main)
            .into(holder.itemView.campaign_image_iv)
    }

}

class CampaignImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)