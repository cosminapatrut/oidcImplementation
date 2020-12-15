package com.orange.volunteers.campaigns.overview

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.orange.domain.model.CampaignAddress
import com.orange.domain.model.CampaignCoordinates
import com.orange.domain.model.CampaignModel
import com.orange.domain.model.CampaignPicturesModel
import com.orange.volunteers.R
import com.orange.volunteers.campaigns.details.CampaignDetailsFragment
import com.orange.volunteers.campaigns.overview.carouselTransformer.CampaignPageTransformer
import com.orange.volunteers.home.HomeActivity
import com.orange.volunteers.util.Utils
import kotlinx.android.synthetic.main.campaign_image_viewpager_fragment.*
import kotlinx.android.synthetic.main.fragment_campaigns_overview.*
import kotlinx.android.synthetic.main.history_active_campaigns_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.max

class CampaignsFragment : Fragment() {

    private lateinit var campaignsCarouselViewPagerAdapter: CampaignsCarouselViewPagerAdapter
//    private lateinit var campaignsDescriptionAdapter: CampaignsDescriptionAdapter
    private lateinit var selectedCampaignDetailsParam: CampaignDetailsFragment.Companion.CampaignDetailsParameters

    private lateinit var campaignPageTransformer: CampaignPageTransformer
    private val campaignsViewModel by viewModel<CampaignsViewModel>()

    init {
        selectedCampaignDetailsParam =
            CampaignDetailsFragment.Companion.CampaignDetailsParameters(
                "",
                arrayListOf(),
                "",
                "",
                "",
                "",
                CampaignAddress(
                    CampaignCoordinates(0.0, 0.0),
                "",
                "",
                "",
                "",
                ""),
                0,
                0
            )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_campaigns_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setupCampaignsDescriptionAdapter()
        setupCampaignsImagesAdapter()
        setupClickEvents()
        getData()
    }

    fun getData() {
        campaignsViewModel.getCampaigns()
        campaignsViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            it?.let {
                loading_indicator.visibility = if (it) View.VISIBLE else View.GONE
                campaigns_overview_rl.visibility = if (it) View.INVISIBLE else View.VISIBLE
            }
        })
    }

    fun setupCampaignsDescriptionAdapter() {
//        val linearLayoutManager = LinearLayoutManager(
//            context,
//            LinearLayoutManager.HORIZONTAL,
//            false)
//        campaign_overview_rv.layoutManager = linearLayoutManager
//
//        campaignsViewModel.data.observe(viewLifecycleOwner, Observer {
//            context?.let { ctx ->
//                it.let {
//                    campaignsDescriptionAdapter = CampaignsDescriptionAdapter(requireContext(), it)
//                    campaign_overview_rv.adapter = campaignsDescriptionAdapter
//                }
//            }
//        })
//
//        val snapHelper = LinearSnapHelper()
//        snapHelper.attachToRecyclerView(campaign_overview_rv)
//        campaign_overview_rv.scrollToPosition(1)

    }

    fun setupCampaignsImagesAdapter() {
        campaignsViewModel.data.observe(viewLifecycleOwner, Observer {
            context?.let { ctx ->
                it.let {
                    campaignsCarouselViewPagerAdapter =
                        CampaignsCarouselViewPagerAdapter(
                            childFragmentManager,
                            it
                        )
                    campaigns_images_viewpager.adapter = campaignsCarouselViewPagerAdapter
                    campaignsCarouselViewPagerAdapter.notifyDataSetChanged()
                }
            }

            campaigns_number_tv.text = campaignsViewModel.data.value?.size.toString()

        })

        campaigns_images_viewpager.apply{
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels
            val screenWidthDp = screenWidth / context.resources.displayMetrics.density

            val cardSize = 220
            val gapDp = max((screenWidthDp - cardSize - 48).toInt(), 0) / 2f//48 comes from padding

            val gap = Utils.dp2px(resources, gapDp * 1f).toInt()
            offscreenPageLimit = 3
            setPadding(gap, 0, gap, 0)
            campaignPageTransformer =
                CampaignPageTransformer(
                    campaigns_images_viewpager
                )

            pageMargin = -gap / 3

            setPageTransformer(true, campaignPageTransformer, View.LAYER_TYPE_NONE)

            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                var first = true
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

//                    if (first && (positionOffset == 0f) && positionOffsetPixels == 0){
//                        onPageSelected(0)
//                        first = false
//                    }
//
//                    onPageSelected(position)
                }

                override fun onPageSelected(position: Int) {
                    updateSelectedPage(position)
                }
            })

            addOnAdapterChangeListener { _, _, newAdapter ->
                updateSelectedPage(0)
            }

        }

        campaignsViewModel.showError.observe(viewLifecycleOwner, Observer {
            it?.let {
                campaigns_network_error.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

    }

    private fun updateSelectedPage(position: Int) {
        val data = campaignsCarouselViewPagerAdapter.items.getOrNull(position) as? CampaignModel

        campaigns_images_viewpager.setCurrentItem(position, true)
        data?.let {
            context?.let { context ->
                Glide.with(context)
                    .load(it.pictures)
                    .into(campaign_image_iv)

            }
            campaign_title_card_overview.text = it.name
            campaign_summary_card_overview.text = it.shortDescription

            //Send campaign detail params to detailed fragment
            updateSelectedCampaignDetailsParam(
                it.uuid,
                it.pictures,
                it.name,
                it.fullDescription,
                it.startDate,
                it.endDate,
                it.address,
                it.occupancy,
                it.capacity
            )
        }

    }

    private fun updateSelectedCampaignDetailsParam
        (id: String?,
         images: List<CampaignPicturesModel>,
         title: String?,
         summary: String?,
         startDate: String,
         endDate: String,
         address: CampaignAddress?,
         enrolledUsers: Int?,
         usersCapacity: Int?
        ) {
            selectedCampaignDetailsParam =
                CampaignDetailsFragment.Companion.CampaignDetailsParameters(
                    id,
                    images,
                    title,
                    summary,
                    startDate,
                    endDate,
                    address,
                    enrolledUsers,
                    usersCapacity
                )
    }

    private fun setupClickEvents() {
        campaign_details_btn.setOnClickListener {
            (activity as HomeActivity).openCampaignDetailsFragment(selectedCampaignDetailsParam)

        }
    }

    companion object {
        fun newInstance(): CampaignsFragment {
            return CampaignsFragment()
        }
    }
}