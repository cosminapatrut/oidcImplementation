package com.orange.volunteers.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.orange.volunteers.MainAdapter
import com.orange.volunteers.R
import com.orange.volunteers.home.HomeActivity
import com.orange.volunteers.util.InternetConnectionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.fragment_auth.*
import org.joda.time.tz.ZoneInfoCompiler.main


class AuthFragment : Fragment() {
    private val disposable: CompositeDisposable = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        monitorNetworkConnectivity()
        setupHomeRecyclerView()
        setupDotsIndicator()
        setupClickEvents()
    }

    private fun monitorNetworkConnectivity() {
        InternetConnectionManager
            .monitorInternetConnection(requireContext())
            .subscribeOn(io())
            .observeOn(
                AndroidSchedulers.mainThread())
            .subscribe({
                if(it) {
                    if (isAdded) {
                        auth_network_error.visibility = View.GONE
                    }
                } else {
                    if (isAdded) {
                        auth_network_error.visibility = View.VISIBLE
                    }
                }
            }, {
                Log.d("SmartVoiceKit", "$it")
            })
            .addTo(disposable)
    }

    private fun setupHomeRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false)
        home_horizontal_rv.layoutManager = linearLayoutManager

        val homeAdapter = MainAdapter(
            requireContext(),
            items = mockHomeRecyclerItems()
        )
        home_horizontal_rv.adapter = homeAdapter
        home_horizontal_rv.scrollToPosition(1)
    }

    private fun setupDotsIndicator() {
        val snapHelper =  PagerSnapHelper()
        snapHelper.attachToRecyclerView(home_horizontal_rv)
        //Setup dots indicator
        val circleIndicator = circle_indicator
        circleIndicator.attachToRecyclerView(home_horizontal_rv, snapHelper)
    }

    private fun setupClickEvents() {

        start_auth.setOnClickListener {
//            val previousFragment = getCallerFragment()
//            when(previousFragment) {
//
//                "campaignDetailsFragment" -> {
                    (activity as HomeActivity).startAuth(100)

//                }
//            }
            TokenManager.didUserLogOut = false

        }
    }

    private fun mockHomeRecyclerItems(): List<com.orange.domain.model.HomeRecyclerItem> = listOf(
        com.orange.domain.model.HomeRecyclerItem(
            R.drawable.ic_view_pager_main,
            "#PentruMaine",
            "Devino voluntar in campania noastra pentru a ajuta prima linie in lupta cu COVID-19"
        ),
        com.orange.domain.model.HomeRecyclerItem(
            R.drawable.ic_view_pager_main,
            "#PentruMaine",
            "Devino voluntar in campania noastra pentru a ajuta prima linie in lupta cu COVID-19"
        ),
        com.orange.domain.model.HomeRecyclerItem(
            R.drawable.ic_view_pager_main,
            "#PentruMaine",
            "Devino voluntar in campania noastra pentru a ajuta prima linie in lupta cu COVID-19"
        )
    )

    private fun getCallerFragment(): String? {
        val fm: FragmentManager? = fragmentManager
        val count = fragmentManager!!.backStackEntryCount
        return fm?.getBackStackEntryAt(count - 2)?.name
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    companion object {
        fun newInstance(): AuthFragment {
            return AuthFragment()
        }
    }
}