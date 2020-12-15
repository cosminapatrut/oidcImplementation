package com.orange.volunteers.history.activeCampaigns

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.orange.volunteers.R
import com.orange.volunteers.campaigns.details.CampaignDetailsViewModel
import com.orange.volunteers.history.HistoryAdapter
import com.orange.volunteers.history.YearlyCampaigns
import com.orange.volunteers.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.campaign_details_fragment.*
import kotlinx.android.synthetic.main.fragment_campaigns_overview.*
import kotlinx.android.synthetic.main.history_active_campaigns_fragment.*
import kotlinx.android.synthetic.main.history_active_campaigns_fragment.history_campaigns_rv
import kotlinx.android.synthetic.main.history_past_campaigns_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class HistoryActiveCampaignsFragment() : Fragment() {

    val activeCampaignsViewModel by viewModel<HistoryActiveCampaignsViewModel>()
    val campaignDetailsViewModel by viewModel<CampaignDetailsViewModel>()

    lateinit var historyAdapter: HistoryAdapter
    protected val autoDisposable = AutoDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autoDisposable.bindTo(lifecycle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.history_active_campaigns_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHistoryAdapter()
        getData()
        observeEnrollmentStatus()
        observeRxBusEvents()
    }

    private fun getData() {
        activeCampaignsViewModel.getEnrollments()
        activeCampaignsViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            it?.let {
                active_loading_indicator.visibility = if (it) View.VISIBLE else View.GONE
            }
        })
        activeCampaignsViewModel.showError.observe(viewLifecycleOwner, Observer {
            it?.let {
                active_network_error.visibility = if (it) View.VISIBLE else View.GONE
            }
        })
    }

    private fun setupHistoryAdapter() {
        activeCampaignsViewModel.enrollments.observe(viewLifecycleOwner, Observer {
            context?.let { ctx ->

                val linearLayoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                history_campaigns_rv.layoutManager = linearLayoutManager
                //TODO show attend button
                historyAdapter =
                    HistoryAdapter(
                        ctx,
                        it,
                        true
                    )
                history_campaigns_rv.adapter = historyAdapter
                historyAdapter.notifyDataSetChanged()

                if(historyAdapter.itemCount == 0) {
                    active_empty.visibility = View.VISIBLE
                } else {
                    active_empty.visibility = View.GONE
                }
            }
        })

    }

    private fun observeRxBusEvents() {
        RxBus.listen(EventSubscribeToCampaign::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                campaignDetailsViewModel.enrollToCampaign(it.campaignId)
                //TODO set text to "Renunta"

            }.addTo(autoDisposable)
        RxBus.listen(EventUnsubscribeFromEnrollment::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {

            }
            .doOnError {

            }
            .subscribe {
                campaignDetailsViewModel.cancelEnrollment(it.enrollmentId)
                //TODO set text to "Participa"
            }
            .addTo(autoDisposable)
    }

    fun observeEnrollmentStatus() {
        activeCampaignsViewModel.isUserEnrolled.observe(viewLifecycleOwner, Observer {
            if (it) {
                subscribe_btn.text = "Renunta"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    subscribe_btn.background = context?.getDrawable(R.drawable.button_rounded_grey_fill)
                }
            } else {
                subscribe_btn.text = "Participa"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    subscribe_btn.background = context?.getDrawable(R.drawable.button_rounded_orange_fill)
                }
            }
        })
    }

    companion object {
        fun newInstance(): HistoryActiveCampaignsFragment {
            return HistoryActiveCampaignsFragment()
        }
    }
}