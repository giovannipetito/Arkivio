@file:Suppress("DEPRECATION")

package it.giovanni.kotlin.fragments.detail.nearby.chat

import android.content.DialogInterface
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AppIdentifier
import com.google.android.gms.nearby.connection.AppMetadata
import com.google.android.gms.nearby.connection.Connections.*
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.DetailFragment
import kotlinx.android.synthetic.main.nearby_chat_layout.*
import java.util.*

class NearbyChatFragment: DetailFragment(), ConnectionCallbacks, OnConnectionFailedListener, MessageListener {

    // Once the devices are connected, they can send messages to each other.

    private val TAG = "NearbyChatFragment"
    private var viewFragment: View? = null

    /**
     * Timeouts (in millis) for startAdvertising and startDiscovery.  At the end of these time
     * intervals the app will silently stop advertising or discovering.
     *
     * To set advertising or discovery to run indefinitely, use 0L where timeouts are required.
     */
    private val TIMEOUT_ADVERTISE = 1000L * 30L
    private val TIMEOUT_DISCOVER = 1000L * 30L

    /**
     * Possible states for this application:
     * IDLE - GoogleApiClient not yet connected, can't do anything.
     * READY - GoogleApiClient connected, ready to use Nearby Connections API.
     * ADVERTISING - advertising for peers to connect.
     * DISCOVERING - looking for a peer that is advertising.
     * CONNECTED - found a peer.
     */
    companion object {
        private const val STATE_IDLE = 1023
        private const val STATE_READY = 1024
        private const val STATE_ADVERTISING = 1025
        private const val STATE_DISCOVERING = 1026
        private const val STATE_CONNECTED = 1027
    }

    @kotlin.annotation.Retention(AnnotationRetention.BINARY) // In Java: @Retention(RetentionPolicy.CLASS)
    @IntDef(STATE_IDLE, STATE_READY, STATE_ADVERTISING, STATE_DISCOVERING, STATE_CONNECTED)
    internal annotation class NearbyConnectionState

    /**
     * GoogleApiClient for connecting to the Nearby Connections API
     */
    private var googleApiClient: GoogleApiClient? = null

    /**
     * Views and Dialogs
     */
    private var nearbyDialog: NearbyDialog? = null

    private var editMessage: EditText? = null
    private var debugText: TextView? = null

    /**
     * The endpoint ID of the connected peer, used for messaging
     */
    private var otherEndpointId: String? = null

    override fun getLayout(): Int {
        return R.layout.nearby_chat_layout
    }

    override fun getTitle(): Int {
        return R.string.nearby_chat_title
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

    private var connectionRequestListener: ConnectionRequestListener = object : ConnectionRequestListener() {

        override fun onConnectionRequest(s: String, s1: String, bytes: ByteArray) {
            this@NearbyChatFragment.onConnectionRequest(s, s1)
        }
    }

    private var endpointDiscoveryListener: EndpointDiscoveryListener = object : EndpointDiscoveryListener() {

        override fun onEndpointFound(s: String, s1: String, s2: String) {
            this@NearbyChatFragment.onEndpointFound(s, s2)
        }

        override fun onEndpointLost(s: String) {
            this@NearbyChatFragment.onEndpointLost(s)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editMessage = viewFragment?.findViewById(R.id.edittext_message)
        debugText = viewFragment?.findViewById(R.id.debug_text)

        // Debug text view
        debugText!!.movementMethod = ScrollingMovementMethod()

        googleApiClient = GoogleApiClient.Builder(context!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(Nearby.CONNECTIONS_API)
            .build()

        button_advertise.setOnClickListener {
            startAdvertising()
        }

        button_discover.setOnClickListener {
            startDiscovery()
        }

        button_send.setOnClickListener {
            sendMessage()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
        googleApiClient!!.connect()
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")

        // Disconnect the Google API client and stop any ongoing discovery or advertising. When the
        // GoogleAPIClient is disconnected, any connected peers will get an onDisconnected callback.
        if (googleApiClient != null) {
            googleApiClient!!.disconnect()
        }
    }

    /**
     * Begin advertising for Nearby Connections, if possible.
     */
    private fun startAdvertising() {
        debugLog("startAdvertising")
        /*
        if (!isOnWiFiConnection()) {
            debugLog("startAdvertising: not connected to WiFi network.")
            return
        }
        */

        // Advertising with an AppIdentifer lets other devices on the network discover this
        // application and prompt the user to install the application.
        val appIdentifierList: MutableList<AppIdentifier> = ArrayList()
        appIdentifierList.add(AppIdentifier(currentActivity.packageName))
        val appMetadata = AppMetadata(appIdentifierList)

        // Advertise for Nearby Connections. This will broadcast the service id defined in
        // AndroidManifest.xml. By passing 'null' for the name, the Nearby Connections API
        // will construct a default name based on device model such as 'LGE Nexus 5'.
        val name: String? = null
        Nearby.Connections.startAdvertising(
            googleApiClient,
            name,
            appMetadata,
            TIMEOUT_ADVERTISE,
            connectionRequestListener
        ).setResultCallback { result: StartAdvertisingResult ->
            Log.i(TAG, "startAdvertising:onResult: $result")
            if (result.status.isSuccess) {
                debugLog("startAdvertising:onResult: SUCCESS")
                updateViewVisibility(STATE_ADVERTISING)
            } else {
                debugLog("startAdvertising:onResult: FAILURE ")

                // If the user hits 'Advertise' multiple times in the timeout window, the error will
                // be STATUS_ALREADY_ADVERTISING
                val statusCode = result.status.statusCode
                if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
                    debugLog("STATUS_ALREADY_ADVERTISING")
                } else {
                    updateViewVisibility(STATE_READY)
                }
            }
        }
    }

    /**
     * Begin discovering devices advertising Nearby Connections, if possible.
     */
    private fun startDiscovery() {
        debugLog("startDiscovery")
        /*
        if (!isOnWiFiConnection()) {
            debugLog("startDiscovery: not connected to WiFi network.")
            return
        }
        */

        // Discover nearby apps that are advertising with the required service ID.
        val serviceId = getString(R.string.service_id)
        Nearby.Connections.startDiscovery(
            googleApiClient,
            serviceId,
            TIMEOUT_DISCOVER,
            endpointDiscoveryListener
        ).setResultCallback { status: Status ->
            if (status.isSuccess) {
                debugLog("startDiscovery:onResult: SUCCESS")
                updateViewVisibility(STATE_DISCOVERING)
            } else {
                debugLog("startDiscovery:onResult: FAILURE")

                // If the user hits 'Discover' multiple times in the timeout window,
                // the error will be STATUS_ALREADY_DISCOVERING
                val statusCode = status.statusCode
                if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_DISCOVERING) {
                    debugLog("STATUS_ALREADY_DISCOVERING")
                } else {
                    updateViewVisibility(STATE_READY)
                }
            }
        }
    }

    /**
     * Send a reliable message to the connected peer. Takes the contents of the EditText and
     * sends the message as a byte[].
     */
    private fun sendMessage() {
        // Sends a reliable message, which is guaranteed to be delivered eventually and to respect
        // message ordering from sender to receiver. Nearby.Connections.sendUnreliableMessage should
        // be used for high-frequency messages where guaranteed delivery is not required, such as
        // showing one player's cursor location to another. Unreliable messages are often delivered
        // faster than reliable messages.
        val message = editMessage!!.text.toString()
        Nearby.Connections.sendReliableMessage(googleApiClient, otherEndpointId, message.toByteArray())
        editMessage!!.text = null
    }

    /**
     * Send a connection request to a given endpoint.
     *
     * @param endpointId   the endpointId to which you want to connect.
     * @param endpointName the name of the endpoint to which you want to connect. Not required to
     * make the connection, but used to display after success or failure.
     */
    private fun connectTo(endpointId: String, endpointName: String) {

        debugLog("connectTo: $endpointId: $endpointName")

        // Send a connection request to a remote endpoint. By passing 'null' for the name,
        // the Nearby Connections API will construct a default name based on device model
        // such as 'LGE Nexus 5'.
        val myName = "Giovanni"
        Nearby.Connections.sendConnectionRequest(
            googleApiClient, myName, endpointId, null, { endpointId1: String, status: Status, _: ByteArray? ->
                Log.i(TAG, "onConnectionResponse: $endpointId1: $status")
                if (status.isSuccess) {
                    debugLog("onConnectionResponse: $endpointName SUCCESS")
                    Toast.makeText(context, "Connected to $endpointName", Toast.LENGTH_SHORT).show()
                    otherEndpointId = endpointId1
                    updateViewVisibility(STATE_CONNECTED)
                } else {
                    debugLog("onConnectionResponse: $endpointName FAILURE")
                }
            }, this
        )
    }

    fun onConnectionRequest(endpointId: String, endpointName: String) {

        debugLog("onConnectionRequest:$endpointId:$endpointName")

        // This device is advertising and has received a connection request. Show a dialog asking
        // the user if they would like to connect and accept or reject the request accordingly.
        val mConnectionRequestDialog = AlertDialog.Builder(context!!)
            .setTitle("Connection Request")
            .setMessage("Do you want to connect to $endpointName?")
            .setCancelable(false)
            .setPositiveButton("Connect") { _: DialogInterface?, _: Int ->
                Nearby.Connections.acceptConnectionRequest(
                    googleApiClient,
                    endpointId,
                    null,
                    this@NearbyChatFragment
                ).setResultCallback { status: Status ->
                    if (status.isSuccess) {
                        debugLog("acceptConnectionRequest: SUCCESS")
                        otherEndpointId = endpointId
                        updateViewVisibility(STATE_CONNECTED)
                    } else {
                        debugLog("acceptConnectionRequest: FAILURE")
                    }
                }
            }.setNegativeButton("No") { _: DialogInterface?, _: Int ->
                Nearby.Connections.rejectConnectionRequest(
                    googleApiClient,
                    endpointId
                )
            }.create()

        mConnectionRequestDialog.show()
    }

    fun onEndpointFound(endpointId: String, endpointName: String) {

        Log.i(TAG, "onEndpointFound:$endpointId:$endpointName")

        // This device is discovering endpoints and has located an advertiser. Display a dialog to
        // the user asking if they want to connect, and send a connection request if they do.
        if (nearbyDialog == null) {
            // Configure the AlertDialog that the MyListDialog wraps
            val builder = AlertDialog.Builder(context!!)
                .setTitle("Endpoint(s) Found")
                .setCancelable(true)
                .setNegativeButton("Cancel") { _: DialogInterface?, _: Int ->
                    nearbyDialog!!.dismiss()
                }

            // Create the MyListDialog with a listener
            nearbyDialog = NearbyDialog(context!!, builder, DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                val selectedEndpointName = nearbyDialog!!.getItemKey(which)
                val selectedEndpointId = nearbyDialog!!.getItemValue(which)
                this@NearbyChatFragment.connectTo(selectedEndpointId!!, selectedEndpointName)
                nearbyDialog!!.dismiss()
            })
        }
        nearbyDialog?.addItem(endpointName, endpointId)
        nearbyDialog?.show()
    }

    fun onEndpointLost(endpointId: String) {
        debugLog("onEndpointLost: $endpointId")

        // An endpoint that was previously available for connection is no longer. It may have stopped advertising,
        // gone out of range, or lost connectivity. Dismiss any dialog that  was offering a connection.
        if (nearbyDialog != null) {
            nearbyDialog!!.removeItemByValue(endpointId)
        }
    }

    override fun onConnected(p0: Bundle?) {
        debugLog("onConnected")
        updateViewVisibility(STATE_READY)
    }

    override fun onConnectionSuspended(i: Int) {
        debugLog("onConnectionSuspended: $i")
        updateViewVisibility(STATE_IDLE)

        // Try to re-connect
        googleApiClient!!.reconnect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        debugLog("onConnectionFailed: $connectionResult")
        updateViewVisibility(STATE_IDLE)
    }

    override fun onMessageReceived(endpointId: String?, payload: ByteArray?, isReliable: Boolean) {
        // A message has been received from a remote endpoint.
        debugLog("onMessageReceived: " + endpointId + ": " + String(payload!!))
    }

    override fun onDisconnected(endpointId: String?) {
        debugLog("onDisconnected: $endpointId")
        updateViewVisibility(STATE_READY)
    }

    /**
     * Change the application state and update the visibility on on-screen views '
     * based on the new state of the application.
     *
     * @param newState the state to move to (should be NearbyConnectionState)
     */
    private fun updateViewVisibility(@NearbyConnectionState newState: Int) {
        when (newState) {
            STATE_IDLE -> {
                // The GoogleAPIClient is not connected, we can't yet start advertising or discovery so hide all buttons
                buttons_container.visibility = View.GONE
                message_container.visibility = View.GONE
            }
            STATE_READY -> {
                // The GoogleAPIClient is connected, we can begin advertising or discovery.
                buttons_container.visibility = View.VISIBLE
                message_container.visibility = View.GONE
            }
            STATE_CONNECTED -> {
                // We are connected to another device via the Connections API, so we can show the message UI.
                buttons_container.visibility = View.VISIBLE
                message_container.visibility = View.VISIBLE
            }
            STATE_ADVERTISING, STATE_DISCOVERING -> {}
        }
    }

    /**
     * Print a message to the DEBUG LogCat as well as to the on-screen debug panel.
     *
     * @param message the message to print and display.
     */
    private fun debugLog(message: String) {
        Log.i(TAG, message)
        debugText!!.append("""$message """.trimIndent()) // In Java: debugText.append("\n" + message);
    }
}