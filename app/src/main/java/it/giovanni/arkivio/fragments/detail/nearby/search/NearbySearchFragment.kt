@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.fragments.detail.nearby.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.ListView
import androidx.appcompat.widget.SwitchCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*
import com.google.android.material.snackbar.Snackbar
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.detail.nearby.search.DeviceMessage.Companion.fromNearbyMessage
import it.giovanni.arkivio.fragments.detail.nearby.search.DeviceMessage.Companion.newNearbyMessage
import java.util.*

class NearbySearchFragment: DetailFragment(), ConnectionCallbacks, OnConnectionFailedListener {

    private val mTag = NearbySearchFragment::class.java.simpleName
    private var viewFragment: View? = null

    private val ttlSeconds = 3 * 60 // Three minutes.

    private val keyUUID = "key_uuid" // Key used in writing to and reading from SharedPreferences.

    // Sets the time in seconds for a published message or a subscription to live.
    private val pubSubStrategy = Strategy.Builder().setTtlSeconds(ttlSeconds).build()

    // The entry point to Google Play Services.
    private var googleApiClient: GoogleApiClient? = null

    // The Message object used to broadcast information about the device to nearby devices.
    private var message: Message? = null

    // A MessageListener for processing messages from nearby devices.
    private var messageListener: MessageListener? = null

    // Adapter for working with messages from nearby publishers.
    private var nearbyDevicesAdapter: ArrayAdapter<String>? = null

    var publishSwitch: SwitchCompat? = null
    var subscribeSwitch: SwitchCompat? = null

    // Creates a UUID and saves it to SharedPreferences. The UUID is added to the published
    // message to avoid it being undelivered due to de-duplication.
    private fun getUUID(sharedPreferences: SharedPreferences): String? {

        var uuid = sharedPreferences.getString(keyUUID, "")
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString()
            sharedPreferences.edit().putString(keyUUID, uuid).apply()
        }
        return uuid
    }

    override fun getLayout(): Int {
        return R.layout.nearby_search_layout
    }

    override fun getTitle(): Int {
        return R.string.nearby_search_title
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

        publishSwitch = viewFragment?.findViewById(R.id.publish_switch)
        subscribeSwitch = viewFragment?.findViewById(R.id.subscribe_switch)

        publishSwitch!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            // If GoogleApiClient is connected, perform sub actions in response to user action.
            // If it isn't connected, do nothing, and perform sub actions when it connects (see onConnected()).
            if (googleApiClient != null && googleApiClient?.isConnected!!) {
                if (isChecked)
                    subscribe()
                else
                    unsubscribe()
            }
        }

        subscribeSwitch!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            // If GoogleApiClient is connected, perform pub actions in response to user action.
            // If it isn't connected, do nothing, and perform pub actions when it connects (see onConnected()).
            if (googleApiClient != null && googleApiClient?.isConnected!!) {
                if (isChecked)
                    publish()
                else
                    unpublish()
            }
        }

        // Build the message that is going to be published. This contains the device name and a UUID.
        message = newNearbyMessage(getUUID(currentActivity.getSharedPreferences(context?.packageName, Context.MODE_PRIVATE)), Build.MODEL)

        messageListener = object : MessageListener() {

            override fun onFound(message: Message) {
                // Called when a new message is found.
                nearbyDevicesAdapter!!.add(fromNearbyMessage(message)?.getMessageBody())
            }

            override fun onLost(message: Message) {
                // Called when a message is no longer detectable nearby.
                nearbyDevicesAdapter!!.remove(fromNearbyMessage(message)?.getMessageBody())
            }
        }

        val nearbyDevicesArrayList: List<String> = ArrayList()
        nearbyDevicesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, nearbyDevicesArrayList)
        val nearbyDevicesListView: ListView = viewFragment!!.findViewById(R.id.nearby_devices_listview)
        nearbyDevicesListView.adapter = nearbyDevicesAdapter
        buildGoogleApiClient()
    }

    private fun buildGoogleApiClient() {
        if (googleApiClient != null) {
            return
        }
        googleApiClient = GoogleApiClient.Builder(requireContext())
            .addApi(Nearby.MESSAGES_API)
            .addConnectionCallbacks(this)
            .enableAutoManage(currentActivity, this)
            .build()
    }

    override fun onConnected(bundle: Bundle?) {
        Log.i(mTag, "GoogleApiClient connected")
        // We use the Switch buttons in the UI to track whether we were previously doing pub/sub.
        // Since the GoogleApiClient disconnects when the activity is destroyed, foreground pubs/subs
        // do not survive device rotation. Once this activity is re-created and GoogleApiClient connects,
        // we check the UI and pub/sub again if necessary.
        if (publishSwitch!!.isChecked)
            publish()
        if (subscribeSwitch!!.isChecked)
            subscribe()
    }

    override fun onConnectionSuspended(i: Int) {
        logAndShowSnackbar("Connection suspended. Error code: $i")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        logAndShowSnackbar("Exception while connecting to Google Play services: " + connectionResult.errorMessage)
    }

    // Subscribes to messages from nearby devices and updates the UI if the subscription either fails or TTLs.
    private fun subscribe() {
        Log.i(mTag, "Subscribing")
        nearbyDevicesAdapter!!.clear()

        val options = SubscribeOptions.Builder()
            .setStrategy(pubSubStrategy)
            .setCallback(object : SubscribeCallback() {
                override fun onExpired() {
                    super.onExpired()
                    Log.i(mTag, "No longer subscribing")
                    currentActivity.runOnUiThread { subscribeSwitch!!.isChecked = false }
                }
            }).build()

        Nearby.Messages.subscribe(googleApiClient, messageListener, options).setResultCallback { status: Status ->
                if (status.isSuccess) {
                    Log.i(mTag, "Subscribed successfully.")
                } else {
                    logAndShowSnackbar("Could not subscribe, status = $status")
                    subscribeSwitch!!.isChecked = false
                }
            }
    }

    // Publishes a message to nearby devices and updates the UI if the publication either fails or TTLs.
    private fun publish() {
        Log.i(mTag, "Publishing")

        val options = PublishOptions.Builder()
            .setStrategy(pubSubStrategy)
            .setCallback(object : PublishCallback() {
                override fun onExpired() {
                    super.onExpired()
                    Log.i(mTag, "No longer publishing")
                    currentActivity.runOnUiThread { publishSwitch!!.isChecked = false }
                }
            }).build()

        Nearby.Messages.publish(googleApiClient, message, options).setResultCallback { status: Status ->
                if (status.isSuccess) {
                    Log.i(mTag, "Published successfully.")
                } else {
                    logAndShowSnackbar("Could not publish, status = $status")
                    publishSwitch!!.isChecked = false
                }
            }
    }

    // Stops subscribing to messages from nearby devices.
    private fun unsubscribe() {
        Log.i(mTag, "Unsubscribing.")
        Nearby.Messages.unsubscribe(googleApiClient, messageListener)
    }

    // Stops publishing message to nearby devices.
    private fun unpublish() {
        Log.i(mTag, "Unpublishing.")
        Nearby.Messages.unpublish(googleApiClient, message)
    }

    // Logs a message and shows a Snackbar using the String text that is used in the Log message and the SnackBar.
    private fun logAndShowSnackbar(text: String) {
        Log.i(mTag, text)
        val container: View = viewFragment!!.findViewById(R.id.nearby_search_container)
        Snackbar.make(container, text, Snackbar.LENGTH_LONG).show()
    }
}