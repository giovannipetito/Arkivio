@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.fragments.detail.nearby.beacons

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*
import com.google.android.material.snackbar.Snackbar
import it.giovanni.arkivio.BuildConfig
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import kotlinx.android.synthetic.main.nearby_beacons_layout.*
import java.util.*

class NearbyBeaconsFragment: DetailFragment(),
    ConnectionCallbacks,
    OnConnectionFailedListener,
    OnSharedPreferenceChangeListener {

    private val mTag = NearbyBeaconsFragment::class.java.simpleName
    private var viewFragment: View? = null

    private var subscribed = false
    private val permissionsRequestCode = 1
    private val keySubscribed = "subscribed"
    private var googleApiClient: GoogleApiClient? = null // The entry point to Google Play Services.
    private val messagesList: MutableList<String> = ArrayList()
    private var adapter: ArrayAdapter<String>? = null // Adapter for working with messages from nearby beacons.

    override fun getLayout(): Int {
        return R.layout.nearby_beacons_layout
    }

    override fun getTitle(): Int {
        return R.string.nearby_beacons_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null)
            subscribed = savedInstanceState.getBoolean(keySubscribed, false)

        val cachedMessages = NearbyBeaconsUtils.getCachedMessages(context!!)
        messagesList.addAll(cachedMessages)

        val listView: ListView = viewFragment!!.findViewById(R.id.nearby_messages_listview)
        adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, messagesList)
        listView.adapter = adapter

        if (!havePermissions()) {
            Log.i(mTag, "Requesting permissions needed for this app.")
            requestPermissions()
        }
    }

    override fun onResume() {
        super.onResume()

        currentActivity.getSharedPreferences(context?.packageName, Context.MODE_PRIVATE)
            .registerOnSharedPreferenceChangeListener(this)

        // As part of the permissions workflow, check permissions in case the user has gone to Settings and enable
        // location there. If permissions are adequate, kick off a subscription process by building GoogleApiClient.
        if (havePermissions()) {
            buildGoogleApiClient()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != permissionsRequestCode) {
            return
        }
        for (i in permissions.indices) {
            val permission = permissions[i]
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                // There are states to watch when a user denies permission when presented with the Nearby
                // permission dialog: 1) When the user pressed "Deny", but does not check the "Never ask
                // again" option. In this case, we display a Snackbar which lets the user kick off the
                // permissions flow again. 2) When the user pressed "Deny" and also checked the "Never
                // ask again" option. In this case, the permission dialog will no longer be presented to
                // the user. The user may still want to authorize location and use the app, and we present a
                // Snackbar that directs them to go to Settings where they can grant the location permission.
                if (shouldShowRequestPermissionRationale(permission)) {
                    Log.i(mTag,"Permission denied without 'NEVER ASK AGAIN': $permission")
                    showRequestPermissionsSnackbar()
                } else {
                    Log.i(mTag,"Permission denied with 'NEVER ASK AGAIN': $permission")
                    showLinkToSettingsSnackbar()
                }
            } else {
                Log.i(mTag,"Permission granted, building GoogleApiClient")
                buildGoogleApiClient()
            }
        }
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(context!!)
                .addApi(
                    Nearby.MESSAGES_API, MessagesOptions.Builder()
                        .setPermissions(NearbyPermissions.BLE)
                        .build()
                )
                .addConnectionCallbacks(this)
                .enableAutoManage(currentActivity, this)
                .build()
        }
    }

    override fun onPause() {
        currentActivity.getSharedPreferences(context?.packageName, Context.MODE_PRIVATE)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onConnected(p0: Bundle?) {
        Log.i(mTag, "GoogleApiClient connected")
        // Nearby.Messages.subscribe(...) requires a connected GoogleApiClient. For that reason,
        // we subscribe only once we have confirmation that GoogleApiClient is connected.
        // Nearby.Messages.subscribe(...) requires a connected GoogleApiClient. For that reason,
        // we subscribe only once we have confirmation that GoogleApiClient is connected.
        subscribe()
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.i(mTag, "Connection suspended. Error code: $p0")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        if (nearby_messages_container != null) {
            Snackbar.make(
                nearby_messages_container!!,
                "Exception while connecting to Google Play services: " + p0.errorMessage,
                Snackbar.LENGTH_INDEFINITE
            ).show()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (TextUtils.equals(key, NearbyBeaconsUtils.KEY_CACHED_MESSAGES)) {
            messagesList.clear()
            messagesList.addAll(NearbyBeaconsUtils.getCachedMessages(context!!))
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(keySubscribed, subscribed)
    }

    private fun havePermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            currentActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            permissionsRequestCode
        )
    }

    private fun subscribe() {
        // In this sample, we subscribe when the activity is launched, but not on device orientation change.
        if (subscribed) {
            Log.i(mTag, "Already subscribed.")
            return
        }
        val options = SubscribeOptions.Builder().setStrategy(Strategy.BLE_ONLY).build()
        Nearby.Messages.subscribe(googleApiClient, getPendingIntent(), options).setResultCallback { status: Status ->
            if (status.isSuccess) {
                Log.i(mTag, "Subscribed successfully.")
                currentActivity.startService(getBackgroundSubscribeServiceIntent())
            } else {
                Log.i(mTag,"Operation failed. Error: " + NearbyMessagesStatusCodes.getStatusCodeString(status.statusCode))
            }
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        return PendingIntent.getService(
            context,
            0,
            getBackgroundSubscribeServiceIntent(),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getBackgroundSubscribeServiceIntent(): Intent {
        return Intent(context, NearbyBeaconsService::class.java)
    }

    private fun showLinkToSettingsSnackbar() {
        if (nearby_messages_container == null) {
            return
        }
        Snackbar.make(nearby_messages_container!!, R.string.permission_denied, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.settings) {
                // Build intent that displays the App settings screen.
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }.show()
    }

    private fun showRequestPermissionsSnackbar() {
        if (nearby_messages_container == null) {
            return
        }
        Snackbar.make(nearby_messages_container!!, R.string.permission_required, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.button_ok) {
            // Request permission.
            ActivityCompat.requestPermissions(
                currentActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                permissionsRequestCode
            )
        }.show()
    }
}