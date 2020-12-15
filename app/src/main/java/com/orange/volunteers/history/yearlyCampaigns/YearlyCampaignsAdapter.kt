package com.orange.volunteers.history.yearlyCampaigns

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orange.domain.model.UserEnrollmentModel
import com.orange.domain.model.UserEnrollmentStatus
import com.orange.volunteers.R
import com.orange.volunteers.util.EventSubscribeToCampaign
import com.orange.volunteers.util.EventUnsubscribeFromEnrollment
import com.orange.volunteers.util.RxBus
import kotlinx.android.synthetic.main.history_past_campaign_recycler_item.view.*
import java.text.SimpleDateFormat

class YearlyCampaignsAdapter(
    private val context: Context,
    private val children : List<UserEnrollmentModel>,
    private val showAttendBtn: Boolean = false)
    : RecyclerView.Adapter<YearlyCampaignViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YearlyCampaignViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_past_campaign_recycler_item, parent, false)
        return YearlyCampaignViewHolder(v)
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(holder: YearlyCampaignViewHolder, position: Int) {
        val item = children[position]

        if(!item.campaign.pictures.isNullOrEmpty()) {
            Glide.with(context)
                .load(item.campaign.pictures[0].uri)
                .into(holder.itemView.history_image)
        }
        if(item.campaign.pictures.size == 0) {
            Glide.with(context)
                .load(R.drawable.ic_view_pager_main)
                .into(holder.itemView.history_image)
        }

        holder.itemView.history_title.text = item.campaign.name
        holder.itemView.history_date.text = setDateFormattedValue(item.campaign.endDate)

        if(showAttendBtn) {
            holder.itemView.history_attend.visibility = View.VISIBLE
            when(item.status) {
                UserEnrollmentStatus.CANCELLED, UserEnrollmentStatus.REJECTED -> {
                    showSubscribeButton(holder.itemView)
                }
                else -> {
                    showUnsubscribeButton(holder.itemView)
                }
            }
            holder.itemView.history_attend.setOnClickListener {
                when(iEnrollButtonVisible(holder.itemView)) {
                    true-> {
                        item.campaign.uuid?.let { campaignId ->
                            RxBus.publish(EventSubscribeToCampaign(campaignId = campaignId))
                            showUnsubscribeButton(holder.itemView)
                        }
                    }
                    false -> {
                        RxBus.publish(EventUnsubscribeFromEnrollment(enrollmentId = item.uuid))
                        showSubscribeButton(holder.itemView)
                    }
                }
            }
        } else {
            holder.itemView.history_attend.visibility = View.GONE
        }

    }


    private fun showSubscribeButton(view: View) {
        view.history_attend.text = context.getString(R.string.attend_campaign)
        view.history_attend.setTextColor(getColor(context, R.color.brandOrange))
    }

    private fun iEnrollButtonVisible(view: View): Boolean =
        view.history_attend.text == context.getString(R.string.attend_campaign)

    private fun showUnsubscribeButton(view: View) {
        view.history_attend.text = context.getString(R.string.quit_campaign)
        view.history_attend.setTextColor(getColor(context, R.color.home_grey_text))
    }

    private fun setDateFormattedValue(endDate: String): String {
        val formatter = SimpleDateFormat("dd MMM yyyy")
        val parser = SimpleDateFormat("yyyy-M-dd")
        return try {
            val startDateTruncated = endDate.substringBefore(" ")
            var startDateParsed = parser.parse(startDateTruncated)
            context.getString(R.string.start_date, formatter.format(startDateParsed))
        } catch (ex: Exception) {
            ""
        }
    }

}

class YearlyCampaignViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
