package com.orange.volunteers.home

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.orange.domain.model.DomainModel
import com.orange.domain.model.UserLocation
import com.orange.volunteers.R
import com.orange.volunteers.auth.AuthFragment
import com.orange.volunteers.auth.TokenManager
import com.orange.volunteers.campaigns.details.CampaignDetailsFragment
import com.orange.volunteers.campaigns.overview.CampaignsFragment
import com.orange.volunteers.history.HistoryFragment
import com.orange.volunteers.info.InfoFragment
import com.orange.volunteers.orangeAuth.OrangeAuthActivity
import com.orange.volunteers.user.UserProfileFragment
import com.orange.volunteers.userdomains.DomainsFragment
import com.orange.volunteers.util.EventSetUserLocation
import com.orange.volunteers.util.RxBus
import kotlinx.android.synthetic.main.activity_campaign.*

class HomeActivity : AppCompatActivity() {

    private lateinit var activeFragment: Fragment
    private lateinit var bottomNavigationView:BottomNavigationView
    private var locationManager : LocationManager? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
           currentLocation.latitude = location.latitude
            currentLocation.longitude = location.longitude
            Log.v("LOCATION ", currentLocation.longitude.toString() + " " + currentLocation.latitude.toString())
//            thetext.text = (location.longitude + " " + location.latitude)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign)
        AppCenter.start(
            application, "3fd7e469-0a8a-437a-b484-009287a7446a",
            Analytics::class.java, Crashes::class.java
        )

        setupBottomNavigation()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    fun setupBottomNavigation() {
        bottomNavigationView =
            findViewById<View>(R.id.bottom_navigation) as BottomNavigationView

        bottomNavigationView.menu.getItem(0).isChecked = true
        openCampaignsFragment()

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_campaigns ->
                    openCampaignsFragment()
                R.id.action_history ->
                    openHistoryFragment()
                R.id.action_user_profile -> {
                    openUserProfileFragment()
                }
                R.id.action_info -> {
                    openInfoFragment()
                }
            }
            true
        }
    }

    fun openInfoFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container,
                InfoFragment.newInstance()
            )
            .commit()
        activeFragment =
            InfoFragment.newInstance()
    }

    fun openCampaignsFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container,
                CampaignsFragment.newInstance()
            )
            .commit()
        activeFragment =
            CampaignsFragment.newInstance()
    }

    fun openHistoryFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container,
                HistoryFragment.newInstance()
            )
            .commit()
        activeFragment =
            HistoryFragment.newInstance()
    }

    fun openUserProfileFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, UserProfileFragment.newInstance())
            .commit()
        activeFragment = UserProfileFragment.newInstance()
    }

    fun openDomainsFragment(prefferedDomains: ArrayList<DomainModel>) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, DomainsFragment.newInstance(
                prefferedDomains
            ))
            .addToBackStack("domainsFragment")
            .commit()
        activeFragment = DomainsFragment.newInstance(prefferedDomains)
    }

    fun openCampaignDetailsFragment(selectedCampaignDetailsParam: CampaignDetailsFragment.Companion.CampaignDetailsParameters) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, CampaignDetailsFragment.newInstance(
                selectedCampaignDetailsParam))
            .addToBackStack("campaignDetailsFragment")
            .commit()
        activeFragment = CampaignDetailsFragment.newInstance(selectedCampaignDetailsParam)

    }

    fun openAuthFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, AuthFragment.newInstance())
            .addToBackStack("authFragment")
            .commit()
        activeFragment = AuthFragment.newInstance()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {

        } else {
            supportFragmentManager.popBackStack()
            TokenManager.didUserLogOut = false
            //TODO here ask for location permission
            isLocationPermissionGranted()
        }
    }

    override fun onBackPressed() {
        val fm = supportFragmentManager
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
        } else { }

        when(activeFragment ) {
            is CampaignDetailsFragment -> { }
            is DomainsFragment -> { }
            else -> {
                bottom_navigation.selectedItemId = R.id.action_campaigns
            }
        }
    }

    fun isLocationPermissionGranted(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Acces locatie refuzat")
                    .setMessage("Fara permisiunea de a accesa locatia, unele functionalitati nu vor fi disponibile. Permiteti accesul locatiei dvs.?")
                    .setPositiveButton("ACCEPT"
                    ) { _, _ -> //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )


                    }
                    .setNegativeButton("REFUZ") { dialog, _ ->
                        dialog.dismiss()
                        RxBus.publish(EventSetUserLocation(location = UserLocation(0.0, 0.0)))
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            requestLocationCoordinates()
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    requestLocationCoordinates()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Disable user location
                    RxBus.publish(EventSetUserLocation(location = UserLocation(0.0, 0.0)))
                }
                return
            }
        }
    }

    fun requestLocationCoordinates() {
        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            //Request location updates:
            locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener)

            if(currentLocation.latitude == 0.0
                && currentLocation.longitude == 0.0) {

                if(fusedLocationClient.lastLocation != null) {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        currentLocation.longitude = it.longitude
                        currentLocation.latitude = it.latitude
                        RxBus.publish(EventSetUserLocation(currentLocation))
                    }

                }
            } else {
                RxBus.publish(EventSetUserLocation(location = currentLocation))
            }

        } else {
        // permission denied, boo! Disable the
        // functionality that depends on this permission.
            //TODO send isSharingEnabled=false
            RxBus.publish(EventSetUserLocation(location = UserLocation(0.0, 0.0)))

        }
    }


    fun startAuth(requestCode: Int) {
        startActivityForResult(
            Intent(this, OrangeAuthActivity::class.java),
            100
        )
    }

    companion object {
        var currentLocation = UserLocation(0.0, 0.0)
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

}

