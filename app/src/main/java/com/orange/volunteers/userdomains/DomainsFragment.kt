package com.orange.volunteers.userdomains

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orange.domain.model.DomainModel
import com.orange.domain.model.UpdateUserModel
import com.orange.domain.model.UserResponseModel
import com.orange.volunteers.R
import com.orange.volunteers.home.HomeActivity
import com.orange.volunteers.user.UserProfileViewModel
import com.orange.volunteers.util.DividerItemDecorator
import com.orange.volunteers.util.RxBus
import kotlinx.android.synthetic.main.fragment_domains.*
import kotlinx.android.synthetic.main.fragment_user.*
import org.koin.android.viewmodel.ext.android.viewModel

private const val ARG_ID_DETAILS = "DomainsFragmentList"
private const val LIST_STATE_KEY = "ListState"
class DomainsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_domains, container, false)
    }

    private lateinit var domainsAdapter: DomainsAdapter
    private val domainsViewModel by viewModel<DomainsViewModel>()
    var interestedDomains = arrayListOf<DomainModel>()
    private val userViewModel by viewModel<UserProfileViewModel>()
    lateinit var activeUser: UserResponseModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val args = arguments
//        interestedDomains  =
//            args?.getParcelableArrayList<DomainModel>("DomainsFragmentList")
//                    as ArrayList<DomainModel>

        setupDomainsAdapter()
        setupToolbar()
        getData()
    }

    private fun getData() {
        //TODO from here get user pref domains and it with getActivityDomains
        domainsViewModel.getUserInfo()
        domainsViewModel.getActivityDomains()

    }

    private fun setupDomainsAdapter() {
        domainsViewModel.isLoading.value = true
        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false)
        domains_vertical_rv.layoutManager = linearLayoutManager

        domainsViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            it?.let {
                domains_loading_indicator.visibility = if (it) View.VISIBLE else View.GONE
                domains_container.visibility = if(it) View.INVISIBLE else View.VISIBLE
            }
        })

        domainsViewModel.userData.observe(viewLifecycleOwner, Observer {
            context?.let { ctx ->
                it.let {userResponse ->
                    activeUser = userResponse
                    interestedDomains = userResponse.preferredDomains

                    domainsViewModel.data.observe(viewLifecycleOwner, Observer {
                        context?.let { ctx ->
                            it.let {

                                for (activityDomain in it) {
                                    for(interestedDomain in interestedDomains) {
                                        if((activityDomain.uuid == interestedDomain.uuid))
                                            activityDomain.isInterested = true
                                    }
                                }

                                domainsAdapter = DomainsAdapter(requireContext(), it)
                                domainsAdapter.apply {
                                    onDomainSelectedCallback = { domainSelected ->
                                        interestedDomains.add(domainSelected)
                                        interestedDomains
                                    }
                                    onDomainDeselectedCallback = { domainDeselected ->
                                        //TODO this is just for testing
                                        val unduplicated = interestedDomains.distinctBy {
                                            it.uuid
                                        } as ArrayList<DomainModel>
                                        val newDomainsList =  ArrayList<DomainModel>()

                                        for(item in unduplicated) {
                                            if(item.uuid == domainDeselected.uuid) {

                                            } else {
                                                newDomainsList.add(item)
                                            }
//                                    unduplicated.remove(item)

                                        }

                                        interestedDomains = newDomainsList
                                    }
                                }
                                domains_vertical_rv.adapter = domainsAdapter
                                domainsAdapter.notifyDataSetChanged()
                                domainsViewModel.isLoading.value = false

                            }
                        }
                    })

                }
            }
        })


        domainsViewModel.showError.observe(viewLifecycleOwner, Observer {
            it?.let {
                domains_network_error.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        domains_vertical_rv.scrollToPosition(1)

        val dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(resources.getDrawable(R.drawable.item_divider))
        domains_vertical_rv.addItemDecoration(dividerItemDecoration)

        save_btn.setOnClickListener {

            activeUser.preferredDomains = interestedDomains
             val domainsToUpdate = activeUser.preferredDomains.map {
                it.uuid
             }.toList()
            val userDetailsUpdate = activeUser.location?.let { userLocation ->
                UpdateUserModel(
                    notificationsEnabled = activeUser.notificationsEnabled,
                    domainsOfInterest = domainsToUpdate,
                    locationSharingEnabled = activeUser.locationSharingEnabled,
                    gpsLocation = userLocation

                )
            }

            if (userDetailsUpdate != null) {
                userViewModel.updateUserInfo(userDetailsUpdate)
//                RxBus.publish(EventDomainsUpdated(activeUser.preferredDomains))

            }
            domainsAdapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "Preferintele dvs. au fost actualizate cu succes!", Toast.LENGTH_SHORT).show()
            (activity as HomeActivity).onBackPressed()
        }
    }

    private fun setupToolbar() {
        domains_toolbar.let {
            it.title = ""
            (activity as HomeActivity).setSupportActionBar(it)
            it.setNavigationOnClickListener {
                (activity as HomeActivity).onBackPressed()
            }
        }
    }

    companion object{
        fun newInstance(args: ArrayList<DomainModel>): DomainsFragment {
            return DomainsFragment()
                .apply {
                    val bundle = Bundle()
                    bundle.putParcelableArrayList(
                        ARG_ID_DETAILS,
                        args
                    )
                    arguments = bundle
                }
        }
    }
}

