package com.orange.volunteers.history.pastCampaigns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.orange.domain.model.CampaignAddress
import com.orange.domain.model.CampaignModel
import com.orange.domain.model.UserEnrollmentModel
import com.orange.volunteers.R
import com.orange.volunteers.history.HistoryAdapter
import com.orange.volunteers.history.YearlyCampaigns
import kotlinx.android.synthetic.main.history_active_campaigns_fragment.*
import kotlinx.android.synthetic.main.history_active_campaigns_fragment.history_campaigns_rv
import kotlinx.android.synthetic.main.history_past_campaigns_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class HistoryPastCampaignsFragment() : Fragment() {

    val pastCampaignsViewModel by viewModel<HistoryPastCampaignsViewModel>()

    lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.history_past_campaigns_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHistoryAdapter()
        getData()
    }

    private fun getData() {
        pastCampaignsViewModel.getEnrollments()
        pastCampaignsViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            it?.let {
                past_loading_indicator.visibility = if (it) View.VISIBLE else View.GONE
            }
        })
        pastCampaignsViewModel.showError.observe(viewLifecycleOwner, Observer {
            it?.let {
                past_network_error.visibility = if (it) View.VISIBLE else View.GONE
            }
        })
    }

    private fun setupHistoryAdapter() {
        pastCampaignsViewModel.enrollments.observe(viewLifecycleOwner, Observer {
            context?.let { ctx ->
                val linearLayoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                history_campaigns_rv.layoutManager = linearLayoutManager

                historyAdapter =
                    HistoryAdapter(
                        ctx,
                        it
                    )
                history_campaigns_rv.adapter = historyAdapter
                historyAdapter.notifyDataSetChanged()

                if(historyAdapter.itemCount == 0) {
                    past_empty.visibility = View.VISIBLE
                } else {
                    past_empty.visibility = View.GONE
                }
            }
        })
    }

    companion object {
        fun newInstance(): HistoryPastCampaignsFragment {
            return HistoryPastCampaignsFragment()
        }
    }
}