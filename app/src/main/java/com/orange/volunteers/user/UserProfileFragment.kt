package com.orange.volunteers.user

import android.Manifest
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.orange.domain.model.DomainModel
import com.orange.domain.model.UpdateUserModel
import com.orange.domain.model.UserLocation
import com.orange.domain.model.UserResponseModel
import com.orange.volunteers.R
import com.orange.volunteers.auth.TokenManager
import com.orange.volunteers.home.HomeActivity
import com.orange.volunteers.orangeAuth.OrangeTokenManager
import com.orange.volunteers.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_campaigns_overview.*
import kotlinx.android.synthetic.main.fragment_user.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class UserProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    var domainsOfInterest = arrayListOf<DomainModel>()
    private val userViewModel by viewModel<UserProfileViewModel>()
    var activeUser = UserResponseModel()
    private val disposable: CompositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(OrangeTokenManager.getInstance(requireContext()).isUserAuthorized()) {
            setData()
            observeRxBusEvents()
        }
        else (activity as HomeActivity).openAuthFragment()
        setupClickEvents()
    }

    override fun onPause() {
        super.onPause()
        Log.v("test", "onPause")
    }

    override fun onDetach() {
        super.onDetach()
        Log.v("test", "onDetach")
    }

    override fun onResume() {
        super.onResume()
        setData()
        observeRxBusEvents()
    }

    fun isUserAuthorized(): Boolean {
        val authManager = OrangeTokenManager.getInstance(requireContext())
        if (authManager.authState.isAuthorized
            && !TokenManager.didUserLogOut) {
//            Toast.makeText(requireContext(), "IS authorized",Toast.LENGTH_LONG).show()
            Log.i("TAG", "User is already authenticated, proceeding to home activity")
        } else {
//            Toast.makeText(requireContext(), "NOT authorized",Toast.LENGTH_LONG).show()
            (activity as HomeActivity).openAuthFragment()
            return false
        }
        return true
    }

    fun observeRxBusEvents() {
        RxBus.listen(EventSetUserLocation::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(it.location.longitude != 0.0
                    && it.location.latitude != 0.0) {
                    activeUser.locationSharingEnabled = true
                    activeUser.location = it.location
                    displayLocation(activeUser.location!!)
                    updateUserInfo()
                    switch_location.isChecked = true


                } else {
                    activeUser.locationSharingEnabled = false
                    activeUser.location = it.location
                    displayLocation(activeUser.location!!)
                    //user denied permission, also update it on server
                    updateUserInfo()
                    switch_location.isChecked = false
                }
            }.addTo(disposable)
        RxBus.listen(EventDeleteUserAccountSuccessful::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                disconnect()
                (activity as HomeActivity).setupBottomNavigation()
            }.addTo(disposable)
        RxBus.listen(EventDeleteUserAccountFailed::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                disconnect()
                (activity as HomeActivity).setupBottomNavigation()
            }.addTo(disposable)
    }

    fun setData() {
        userViewModel.getUserInfo()

        userViewModel.data.observe(viewLifecycleOwner, Observer {
            activeUser = it

            context?.let { ctx ->
                it.let {
                    user_email_tv.text = it.contact?.email

                    switch_notifications.isChecked = it.notificationsEnabled

                    if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            == PackageManager.PERMISSION_GRANTED) {
                        //Display user location if enable
                        switch_location.isChecked = it.locationSharingEnabled
                        if(switch_location.isChecked) {
                            if(it.location?.longitude == 0.0 &&
                                it.location?.latitude == 0.0) {

                                //Get user location if is not present on server
                                (activity as HomeActivity).requestLocationCoordinates()
                                it.location = UserLocation(HomeActivity.currentLocation.latitude,
                                    HomeActivity.currentLocation.longitude)
                            }

                            it.location.let { userLocation ->
                                userLocation?.let { it ->
                                    displayLocation(it)
                                }
//                            if (userLocation != null) {
//                                updateUserLocation()
//                            }
                            }
                        } else {
                            //Location is disabled
//                        updateUserLocation()
                            displayLocation(UserLocation(0.0, 0.0))
                        }
                    } else {
                        switch_location.isChecked = false
                        displayLocation(UserLocation(0.0, 0.0))
                    }

                    activeUser.location = it.location

                    domainsOfInterest = it.preferredDomains
                }
            }
        })
        userViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            it?.let {
                user_loading_indicator.visibility = if (it) View.VISIBLE else View.GONE
                user_page_container.visibility = if(it) View.INVISIBLE else View.VISIBLE
            }
        })
        userViewModel.showError.observe(viewLifecycleOwner, Observer {
            it?.let {
                user_network_error.visibility = if (it) View.VISIBLE else View.GONE
            }
        })
    }

    private fun displayLocation(currentLocation: UserLocation ) {
        if(currentLocation.latitude != 0.0 && currentLocation.longitude != 0.0) {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(currentLocation.latitude,
                    currentLocation.longitude, 1)
            val cityName: String = addresses[0].getAddressLine(0)
            pin_tv.visibility = View.VISIBLE
            pin_iv.visibility = View.VISIBLE
            pin_tv.text =   cityName
        } else {
            pin_iv.visibility = View.GONE
            pin_tv.visibility = View.GONE
        }
    }

    private fun setupClickEvents() {
        edit_tv.setOnClickListener {
            (activity as HomeActivity).openDomainsFragment(domainsOfInterest)
        }

        logout_btn.setOnClickListener {
            disconnect()
            (activity as HomeActivity).setupBottomNavigation()
        }

        switch_notifications.setOnClickListener {
            activeUser.notificationsEnabled = switch_notifications.isChecked
            //TODO update user
        }

        switch_location.setOnClickListener {
            //TODO update user
            activeUser.locationSharingEnabled = switch_location.isChecked

            if(switch_location.isChecked) {
                if((activity as HomeActivity).isLocationPermissionGranted()) {

                    activeUser.locationSharingEnabled = true
                    activeUser.location = HomeActivity.currentLocation
                    displayLocation(activeUser.location!!)
                } else {
                    //TODO Request Location permissions
                }
            } else {
                activeUser.location = UserLocation(0.0, 0.0)
                displayLocation(activeUser.location!!)
                //TODO update user
            }
            //   updateUserLocation()
        }

        save_user_btn.setOnClickListener {
            updateUserInfo()
            Toast.makeText(requireContext(), "Contul dvs. a fost actualizat cu succes!", Toast.LENGTH_SHORT).show()
        }

        delete_account_btn.setOnClickListener{

            AlertDialog.Builder(requireContext())
                .setTitle("Stergere profil")
                .setMessage("Sunteti sigur ca doriti sa stergeti profilul dvs. de utilizator?")
                .setCancelable(false)
                .setNegativeButton("ANULARE") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("STERGE") { dialog, _ ->
                    userViewModel.deleteUser()
                    dialog.dismiss()
                }
                .show()

        }
    }

    private fun updateUserInfo() {
        val domainsToUpdate = activeUser.preferredDomains.map {
            it.uuid as String
        }.toTypedArray()

        activeUser.location?.let {
            UpdateUserModel(
                notificationsEnabled = activeUser.notificationsEnabled,
                domainsOfInterest =  domainsToUpdate.toList(),
                locationSharingEnabled = activeUser.locationSharingEnabled,
                gpsLocation = it
            )
        }?.let { userViewModel.updateUserInfo(it) }
    }

    private fun disconnect() {
        val authManager = OrangeTokenManager.getInstance(requireContext())
        authManager.clearToken()
        authManager.clearUserInfo()
        TokenManager.didUserLogOut = true
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    companion object {
        fun newInstance() = UserProfileFragment()
    }
}