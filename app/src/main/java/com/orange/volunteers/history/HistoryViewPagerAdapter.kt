package com.orange.volunteers.history

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.orange.volunteers.history.activeCampaigns.HistoryActiveCampaignsFragment
import com.orange.volunteers.history.pastCampaigns.HistoryPastCampaignsFragment

class HistoryViewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    var steps: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> createActiveCampaignsViewPagerFragment()

            else ->  createHistoryCampaignsViewPagerFragment()
        }
    }

    override fun getCount() = steps.size

    private fun createHistoryCampaignsViewPagerFragment(): HistoryPastCampaignsFragment {
        return HistoryPastCampaignsFragment.newInstance()
    }

    private fun createActiveCampaignsViewPagerFragment(): Fragment {
        return HistoryActiveCampaignsFragment.newInstance()
    }
}
