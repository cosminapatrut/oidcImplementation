package com.orange.volunteers.campaigns.details

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.orange.domain.model.*
import com.orange.volunteers.R
import com.orange.volunteers.campaigns.overview.CampaignsViewModel
import com.orange.volunteers.home.HomeActivity
import com.orange.volunteers.orangeAuth.OrangeTokenManager
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import kotlinx.android.synthetic.main.campaign_details_fragment.*
import kotlinx.android.synthetic.main.fragment_campaigns_overview.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.history_past_campaign_recycler_item.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_ID_DETAILS = "CampaignDetailsFragment::id"

class CampaignDetailsFragment : Fragment() {

    private var selectedCampaignDetailsParam: CampaignDetailsParameters? = null
    private val campaignViewModel by viewModel<CampaignDetailsViewModel>()
    var currentEnrollmentId = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.campaign_details_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Fill datas
        selectedCampaignDetailsParam = arguments?.getParcelable(ARG_ID_DETAILS)

        setupToolbar()
        setupCampaignDetailsRecyclerView()
        observeData()
        observeEnrollmentStatus()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupView()
        }
    }

    fun observeData() {
        campaignViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            it?.let {
                campaign_details_loading_indicator.visibility = if (it) View.VISIBLE else View.GONE
                campaign_details_container.visibility = if (it) View.INVISIBLE else View.VISIBLE
            }
        })

        selectedCampaignDetailsParam?.uuid?.let {
            campaignViewModel.isUserEnrolled(it)
        }


        selectedCampaignDetailsParam?.uuid?.let { campaignViewModel.getEnrollmentId(it) }
        campaignViewModel.isUserEnrolled.observe(viewLifecycleOwner, Observer {
            if (it) {
                subscribe_btn.text = "Renunta"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    subscribe_btn.background = context?.getDrawable(R.drawable.button_rounded_grey_fill)
                }
            } else {
                subscribe_btn.text = "Inscrie-te"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    subscribe_btn.background = context?.getDrawable(R.drawable.button_rounded_orange_fill)
                }
            }

        })
        campaignViewModel.enrollmentID.observe(viewLifecycleOwner, Observer {
            currentEnrollmentId = it
        })
    }

    fun observeEnrollmentStatus() {
        subscribe_btn.setOnClickListener {
            selectedCampaignDetailsParam?.let {
                if (campaignViewModel.isUserEnrolled.value == true) {

                    if(OrangeTokenManager.getInstance(requireContext()).isUserAuthorized()) {
                        it.uuid?.let { id ->
                            campaignViewModel.cancelEnrollment(
                                currentEnrollmentId
                            )
                        }
                        Toast.makeText(
                            context,
                            "Ai renuntat la aceasta campanie",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                            (activity as HomeActivity).openAuthFragment()
                        }
                } else {
                    if(OrangeTokenManager.getInstance(requireContext()).isUserAuthorized()) {
                        it.uuid?.let { id ->
                            campaignViewModel.enrollToCampaign(
                                id
                            )
                        }
                        Toast.makeText(context, "Te-ai inscris cu succes", Toast.LENGTH_SHORT).show()
                    } else {
                        (activity as HomeActivity).openAuthFragment()
                    }

                }
            }
        }
    }

    private fun setupToolbar() {
        campaign_details_toolbar.let {
            it.title = ""
            (activity as HomeActivity).setSupportActionBar(it)
            it.setNavigationOnClickListener {
                (activity as HomeActivity).onBackPressed()
            }
        }
    }

    private fun setupView() {
        setupDotsIndicator()

//        campaign_details_description.movementMethod = ScrollingMovementMethod()

        selectedCampaignDetailsParam?.let {
            title_tv.text = it.name
            campaign_details_description.text = it.fullDescription
            setPeriodValue(it.startDate, it.endDate)

            location_value_tv.text = getString(R.string.city_street_details,
            it.address?.cityName,it.address?.street, it.address?.details )
            volunteers_value_tv.text = getString(R.string.enrolled_capacity, it.enrolledUserCount, it.capacity)
        }

    }

    private fun setupCampaignDetailsRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false)
        campaign_details_horizontal_rv.layoutManager = linearLayoutManager

        val campaignImagesAdapter = this.let {
            selectedCampaignDetailsParam?.images?.let { images ->
                CampaignImagesAdapter(
                    requireContext(),
                    items = images
                )
            }
        }
        campaign_details_horizontal_rv.adapter = campaignImagesAdapter
        campaign_details_horizontal_rv.scrollToPosition(1)
    }

    private fun setupDotsIndicator() {
        val snapHelper =  PagerSnapHelper()
        snapHelper.attachToRecyclerView(campaign_details_horizontal_rv)
        //Setup dots indicator
        val circleIndicator = campaign_details_circle_indicator
        circleIndicator.attachToRecyclerView(campaign_details_horizontal_rv, snapHelper)
    }

    companion object {
        @Parcelize
        data class CampaignDetailsParameters(
            val uuid: String? = "",
            val images: @RawValue List<CampaignPicturesModel> = listOf(),
            val name: String?="",
            val fullDescription: String?="",
            val startDate: String="",
            val endDate: String="",
            val address: @RawValue CampaignAddress?= CampaignAddress(CampaignCoordinates(0.0,0.0),
            "",
            "",
            "",
            "",
            ""),
            val enrolledUserCount: Int?=0,
            val capacity: Int?=0
        ) : Parcelable

        fun newInstance(args: CampaignDetailsParameters): CampaignDetailsFragment {
            return CampaignDetailsFragment()
                .apply {
                val bundle = Bundle()
                bundle.putParcelable(
                    ARG_ID_DETAILS,
                    args
                )
                arguments = bundle
            }
        }
    }

    fun mockCampaignImagesRecyclerItems()= listOf<CampaignImagesModel>(
        CampaignImagesModel("https://responsabilitate-sociala.orange.ro/content/images/2020/04/fiecare-gest-conteaza-1.png"
                ),
        CampaignImagesModel(
                "https://responsabilitate-sociala.orange.ro/content/images/2020/03/Header-1.jpg"
                ),
        CampaignImagesModel(
                "https://responsabilitate-sociala.orange.ro/content/images/2020/03/hdr-supercoders-ilustratie.png"
                )
    )

    fun setPeriodValue(startDate: String, endDate: String) {
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val parser = SimpleDateFormat("yyyy-M-dd")
        try {
            val startDateTruncated = startDate?.substringBefore(" ")
            var startDateParsed = parser.parse(startDateTruncated)

            val endDateTruncated = endDate?.substringBefore(" ")
            var endDateParsed = parser.parse(endDateTruncated)

            period_value_tv.text = getString(R.string.start_date_end_date, formatter.format(startDateParsed), formatter.format(endDateParsed))
        } catch (ex: Exception) {
            period_value_tv.text = ""
        }
    }
}

