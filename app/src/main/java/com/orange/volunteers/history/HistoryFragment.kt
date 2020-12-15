package com.orange.volunteers.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.orange.volunteers.R
import com.orange.volunteers.home.HomeActivity
import com.orange.volunteers.orangeAuth.OrangeTokenManager
import kotlinx.android.synthetic.main.history_fragment.*

class HistoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.history_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setupToolbar()
        if(OrangeTokenManager.getInstance(requireContext()).isUserAuthorized()) {
            setupTabLayout()
            setupViewPagerAdapter()
        } else (activity as HomeActivity).openAuthFragment()
    }

    override fun onResume() {
        super.onResume()
        setupTabLayout()
        setupViewPagerAdapter()
    }

    private fun setupTabLayout() {
        optionTabs.onTabSelected(0)
    }

//    private fun setupToolbar() {
//        history_toolbar.let {
//            it.title = ""
//            (activity as HomeActivity).setSupportActionBar(it)
//            it.setNavigationOnClickListener {
//                (activity as HomeActivity).onBackPressed()
//            }
//        }
//    }

    private fun setupViewPagerAdapter() {
        val historyViewPagerAdapter = HistoryViewPagerAdapter(childFragmentManager)
        historyViewPagerAdapter.steps = listOf("ACTIVE_CAMPAIGNS", "HISTORY_CAMPAIGNS")
        history_viewpager.adapter = historyViewPagerAdapter
        history_viewpager.currentItem = 0

        history_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                historyViewPagerAdapter.getItem(position)
                optionTabs.onTabSelected(position)

            }
        })

        optionTabs.onTabSelectedCallback = {
            history_viewpager.currentItem = it
        }

    }

    companion object {
        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }
}