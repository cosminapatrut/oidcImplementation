package com.orange.volunteers.campaigns.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.orange.domain.model.CampaignModel
import com.orange.volunteers.R

class CampaignsDescriptionAdapter(val ctx: Context,
                                  private val items: List<CampaignModel>
                                  ) : RecyclerView.Adapter<CampaignDescriptionHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(ctx)

//    var onHrefClicked: ((String) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignDescriptionHolder {
        return CampaignDescriptionHolder(
            inflater.inflate(
                R.layout.campaign_description_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CampaignDescriptionHolder, position: Int) {
        var item = items[position]
//        holder.title.text = item.title
//        holder.summary.text = item.summary
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class CampaignDescriptionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    var title: TextView = itemView.findViewById(R.id.campaign_title_card)
//    var summary: TextView = itemView.findViewById(R.id.campaign_summary_card)
}