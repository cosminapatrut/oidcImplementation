package com.orange.volunteers.campaigns.overview

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.orange.domain.model.CampaignModel

class CampaignsCarouselViewPagerAdapter(fragmentManager: FragmentManager,
                                         val items: List<CampaignModel>
) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when(val step = items[position])
        {
            is CampaignModel -> createCampaignViewPagerFragment(step)
//            is RechargeOptionModel -> createRechargeOptionViewPagerFragment(step)
            else -> Fragment()
        }
    }

    override fun getCount() = items.size

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    private fun createCampaignViewPagerFragment(
        data: CampaignModel
    ): CampaignImageViewPagerFragment {
        return CampaignImageViewPagerFragment(
            data
        )
    }
}
