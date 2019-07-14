package it.giovanni.kotlin.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import it.giovanni.kotlin.R
import it.giovanni.kotlin.customview.popup.CustomDialogPopup
import it.giovanni.kotlin.interfaces.IProgressLoader
import it.giovanni.kotlin.utils.Globals

open class GPSActivity : BaseActivity(), IProgressLoader, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    override fun sendFCMToken(token: String?) {}

    private var locationManager: LocationManager? = null
    private var googleApiClient: GoogleApiClient? = null
    private var lastLocation: Location? = null
    private val REQUEST_CHECK_SETTINGS_GPS = 0x1
    private var myLocation: Location? = null
    private val GPS_DISTANCE = 5
    private val GPS_INTERVAL = 2000
    private val REQUEST_CODE_GPS_PERMISSION = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
            // googleApiClient!!.connect()
        }
    }

    override fun onStart() {
        googleApiClient!!.connect()
        super.onStart()
    }

    override fun onStop() {
        googleApiClient!!.disconnect()
        super.onStop()
    }

    override fun onPause() {
        googleApiClient!!.disconnect()
        super.onPause()
    }

    override fun onDestroy() {
        googleApiClient!!.disconnect()
        super.onDestroy()
    }

    fun openGc3() {
        openDetail(Globals.GC3_WEB_VIEW, null)
    }

    private fun coordinates(lat: Double?, lon: Double?) {
        val params = Bundle()
        if (lat != null)
            params.putDouble("lat", lat)
        if (lon != null)
            params.putDouble("lon", lon)
        openDetail(Globals.GC3_WEB_VIEW, params)
        if (googleApiClient != null)
            googleApiClient!!.disconnect()
    }

    var latitudine: Double? = null
    var longitudine: Double? = null
    var waiting = false

    private fun openGC3ByCoordinates() {
        waiting = false
        hideProgressDialog()
        coordinates(latitudine, longitudine)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_GPS_PERMISSION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    waiting = true
                    if (!locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
                        !locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        showGpsSettingsPopup()
                    } else {
                        // locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
                        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_INTERVAL.toLong(), GPS_DISTANCE.toFloat(), locationListenerNetwork)
                        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_INTERVAL.toLong(), GPS_DISTANCE.toFloat(), locationListener)
                        showProgressDialog()
                        getMyLocation()
                        // openGc3()
                    }
                } else {
                    waiting = false
                    hideProgressDialog()
                    openGC3ByCoordinates()
                }
            }
        }
    }

    fun requestGPSPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_GPS_PERMISSION)
        } else {
            if (!locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
                !locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGpsSettingsPopup()
                hideProgressDialog()
            } else {
                waiting = true
                getMyLocation()
            }
        }
    }

    private fun showGpsSettingsPopup() {
        val customPopup = CustomDialogPopup(this, R.style.PopupTheme)
        customPopup.setCancelable(false)
        customPopup.setTitle("")
        customPopup.setType(CustomDialogPopup.TYPE_INFO)
        customPopup.setMessage(resources.getString(R.string.popup_message_enable_gps))
        customPopup.setButtons(
            resources.getString(R.string.popup_button_ok),
            View.OnClickListener {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                customPopup.dismiss()
            },
            resources.getString(R.string.popup_button_cancel),
            View.OnClickListener { customPopup.dismiss() }
        )
        customPopup.show()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        try {
            // val mLocationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
            // location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
            if (location != null) {
                latitudine = location!!.latitude
                longitudine = location!!.longitude
            }
            if (waiting) {
                openGC3ByCoordinates()
            }
        } catch (e: Exception) {
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        hideProgressDialog()
        openGC3ByCoordinates()
    }

    override fun onConnectionSuspended(p0: Int) {}

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient!!.isConnected) {
                myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
                val locationRequest = LocationRequest()
                locationRequest.interval = 3000
                locationRequest.fastestInterval = 3000
                locationRequest.priority = LocationRequest.PRIORITY_NO_POWER
                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                builder.setAlwaysShow(true)
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
                val pendingResult = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build())
                pendingResult.setResultCallback { result ->
                    val status = result.status
                    when (status.statusCode) {
                        LocationSettingsStatusCodes.SUCCESS -> {
                            // All location settings are satisfied.
                            // You can initialize location requests here.
                            myLocation = LocationServices.FusedLocationApi
                                .getLastLocation(googleApiClient)
                            if (myLocation != null) {
                                waiting = false
                                latitudine = myLocation!!.latitude
                                longitudine = myLocation!!.longitude
                            }
                            hideProgressDialog()
                            openGC3ByCoordinates()
                        }
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                            // Location settings are not satisfied.
                            // But could be fixed by showing the user a dialog.
                            try {
                                hideProgressDialog()
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                // Ask to turn on GPS automatically
                                status.startResolutionForResult(this,
                                    REQUEST_CHECK_SETTINGS_GPS)
                            } catch (e: IntentSender.SendIntentException) {
                                // Ignore the error.
                            }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            showGpsSettingsPopup()
                        }
                    }
                    // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                    // finish()
                }
            } else {
                setUpGClient()
            }
        } else {
            setUpGClient()
            // getMyLocation()
        }
    }

    private fun setUpGClient() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }
        googleApiClient!!.connect()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    override fun onLocationChanged(p0: Location?) {
        try {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
            if (lastLocation != null) {
                latitudine = lastLocation!!.latitude
                longitudine = lastLocation!!.longitude
                /*
                if (waiting) {
                    hideProgressDialog()
                    openGC3ByCoordinates()
                }
                */
                if (waiting) openGC3ByCoordinates()
            }
        } catch (e: Exception) {}
    }

    protected var location: Location? = null
    private val locationListener: LocationListener = object : LocationListener {

        override fun onLocationChanged(loc: Location) {
            location = loc
            longitudine = loc.longitude
            latitudine = loc.latitude
            // resolveHumanAddress()
            openGC3ByCoordinates()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    private val locationListenerNetwork: LocationListener = object : LocationListener {

        override fun onLocationChanged(loc: Location) {
            location = loc
            longitudine = loc.longitude
            latitudine = loc.latitude
            // resolveHumanAddress()
            if (waiting) openGC3ByCoordinates()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    override fun showProgressDialog() {}

    override fun hideProgressDialog() {}
}