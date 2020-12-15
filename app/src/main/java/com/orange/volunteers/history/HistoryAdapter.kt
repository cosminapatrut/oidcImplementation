package com.orange.volunteers.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orange.domain.model.CampaignModel
import com.orange.domain.model.UserEnrollmentModel
import com.orange.volunteers.R
import com.orange.volunteers.history.yearlyCampaigns.YearlyCampaignsAdapter
import com.orange.volunteers.util.DividerItemDecorator
import kotlinx.android.synthetic.main.active_campaign_recycler_item.view.*
import kotlinx.android.synthetic.main.history_recycle_item.view.*
import java.text.SimpleDateFormat
import java.util.*

data class YearlyCampaigns(
    val year: Int,
    val nrOfCampaigns: Int?,
    val yearlyCampaignsList: List<CampaignModel>
)

data class YearlyEnrollments(
    val year: Int,
    val nrOfEnrollments: Int?,
    val yearlyEnrollmentsList: List<UserEnrollmentModel>
)


class HistoryAdapter(
    private val context: Context,
    private val items: List<YearlyEnrollments>,
    private val showAttendBtn: Boolean = false
) : RecyclerView.Adapter<HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val headerItemView = layoutInflater.inflate(
            R.layout.history_recycle_item,
            parent,
            false
        )
        return HistoryViewHolder(
            headerItemView
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]

        val calendar = Calendar.getInstance()
        calendar.time = item.yearlyEnrollmentsList[position].campaign.endDate.formatStringToDate()
        val yearOfItem = calendar.get(Calendar.YEAR)

        holder.itemView.campaign_year_tv.text = yearOfItem.toString()

        if(item.nrOfEnrollments == 1) {
            holder.itemView.campaign_nr_tv.text =
                context.getString(R.string.history_campaigns_singular, item.nrOfEnrollments.toString())
        } else {
            holder.itemView.campaign_nr_tv.text =
                context.getString(R.string.history_campaigns_plural, item.nrOfEnrollments.toString())
        }

        val childLayoutManager = LinearLayoutManager(
            holder.itemView.history_yearly_campaigns_rv.context,
            RecyclerView.VERTICAL,
            false
        )

        holder.itemView.history_yearly_campaigns_rv.apply {
            layoutManager = childLayoutManager
            adapter = YearlyCampaignsAdapter(context, item.yearlyEnrollmentsList, showAttendBtn)
            setRecycledViewPool(recycledViewPool)

            this.post{
                (adapter as YearlyCampaignsAdapter).notifyDataSetChanged()
            }
            val dividerItemDecoration: RecyclerView.ItemDecoration =
                DividerItemDecorator(resources.getDrawable(R.drawable.item_divider))
            holder.itemView.history_yearly_campaigns_rv.addItemDecoration(dividerItemDecoration)

        }

    }

    fun String?.formatStringToDate(): Date {
        val parser = SimpleDateFormat("yyyy-M-dd")
        try {
            val endDateTruncated = this?.substringBefore(" ")
            return parser.parse(endDateTruncated)
        } catch (ex: java.lang.Exception) {
            return Date()
        }
    }
}

class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)