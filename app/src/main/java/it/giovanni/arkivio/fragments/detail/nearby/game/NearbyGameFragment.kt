package it.giovanni.arkivio.fragments.detail.nearby.game

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import com.airbnb.paris.extensions.style
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.NearbyGameLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.detail.nearby.game.PlayerGenerator.Companion.generate
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import java.nio.charset.StandardCharsets

class NearbyGameFragment: DetailFragment() {

    private val mTag = NearbyGameFragment::class.java.simpleName

    private var layoutBinding: NearbyGameLayoutBinding? = null
    private val binding get() = layoutBinding

    private val requestCodeRequiredPermissions = 1
    private val strategy = Strategy.P2P_STAR

    private val requiredPermissions: Array<String> = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var connectionsClient: ConnectionsClient  // The handler to Nearby Connections

    private val player = generate()
    private var playerScore = 0

    private var opponent: String? = null
    private var opponentEndpointId: String? = null
    private var opponentScore = 0

    private enum class GameChoice {

        ROCK,
        PAPER,
        SCISSORS;

        fun beats(choice: GameChoice): Boolean {
            return this == ROCK && choice == SCISSORS ||
                    this == SCISSORS && choice == PAPER ||
                    this == PAPER && choice == ROCK
        }
    }

    private var playerChoice: GameChoice? = null
    private var opponentChoice: GameChoice? = null

    // Callbacks for receiving payloads
    private val payloadCallback: PayloadCallback = object : PayloadCallback() {

        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            opponentChoice = GameChoice.valueOf(String(payload.asBytes()!!, StandardCharsets.UTF_8))
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.status == PayloadTransferUpdate.Status.SUCCESS && playerChoice != null && opponentChoice != null) {
                finishRound()
            }
        }
    }

    // Callbacks for connections to other devices
    private val connectionLifecycleCallback: ConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {

        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.i(mTag, "Accepting connection")
            connectionsClient.acceptConnection(endpointId, payloadCallback)
            opponent = connectionInfo.endpointName
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                hideProgressDialog()
                Log.i(mTag, "Connection successful")
                connectionsClient.stopDiscovery()
                connectionsClient.stopAdvertising()
                opponentEndpointId = endpointId
                setOpponentName(opponent!!)
                setStatusText(getString(R.string.status_connected))
                setButtonState(true)
            } else {
                    Log.i(mTag, "Connection failed")
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.i(mTag, "Disconnected from the opponent")
            resetGame()
        }
    }

    // Callbacks for finding other devices
    private val endpointDiscoveryCallback: EndpointDiscoveryCallback = object : EndpointDiscoveryCallback() {

        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.i(mTag, "Endpoint found, connecting")
            if (player != null)
                connectionsClient.requestConnection(player, endpointId, connectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {}
    }

    override fun getLayout(): Int {
        return NO_LAYOUT
    }

    override fun getTitle(): Int {
        return R.string.nearby_game_title
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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = NearbyGameLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewStyle()

        connectionsClient = Nearby.getConnectionsClient(currentActivity)

        binding?.textPlayer?.text = getString(R.string.player_name, player)

        binding?.buttonFindOpponent?.setOnClickListener {
            showProgressDialog()
            startAdvertising()
            startDiscovery()
            setStatusText(getString(R.string.status_searching))
            binding?.buttonFindOpponent?.isEnabled = false
        }

        binding?.buttonDisconnect?.setOnClickListener {
            connectionsClient.disconnectFromEndpoint(opponentEndpointId!!)
            resetGame()
        }

        resetGame()
    }

    override fun onStart() {
        super.onStart()
        if (!hasPermissions(requireContext(), requiredPermissions)) {
            requestPermissions(requiredPermissions, requestCodeRequiredPermissions)
        }
    }

    override fun onStop() {
        connectionsClient.stopAllEndpoints()
        resetGame()
        super.onStop()
    }

    /** Returns true if the app was granted all the permissions. Otherwise, returns false.  */
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /** Handles user acceptance (or denial) of our permission request.  */
    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != requestCodeRequiredPermissions) {
            return
        }
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(context, R.string.error_missing_permissions, Toast.LENGTH_LONG).show()
                currentActivity.finish()
                return
            }
        }
        currentActivity.recreate()
    }

    /** Sends a [GameChoice] to the other player.  */
    fun makeMove(view: View) {
        when (view.id) {
            R.id.button_rock -> {
                sendGameChoice(GameChoice.ROCK)
            }
            R.id.button_paper -> {
                sendGameChoice(GameChoice.PAPER)
            }
            R.id.button_scissors -> {
                sendGameChoice(GameChoice.SCISSORS)
            }
        }
    }

    /** Starts looking for other players using Nearby Connections.  */
    private fun startDiscovery() {
        // Note: Discovery may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startDiscovery(
            currentActivity.packageName,
            endpointDiscoveryCallback,
            DiscoveryOptions.Builder().setStrategy(strategy).build()
        )
    }

    /** Broadcasts our presence using Nearby Connections so other players can find us.  */
    private fun startAdvertising() {
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startAdvertising(
            player,
            currentActivity.packageName,
            connectionLifecycleCallback,
            AdvertisingOptions.Builder().setStrategy(strategy).build()
        )
    }

    /** Wipes all game state and updates the UI accordingly.  */
    private fun resetGame() {
        playerChoice = null
        playerScore = 0
        opponent = null
        opponentEndpointId = null
        opponentChoice = null
        opponentScore = 0
        setOpponentName(getString(R.string.no_opponent_found))
        setStatusText(getString(R.string.status_disconnected))
        updateScore(playerScore, opponentScore)
        setButtonState(false)
    }

    /** Sends the user's selection of rock, paper, or scissors to the opponent.  */
    private fun sendGameChoice(choice: GameChoice) {
        playerChoice = choice
        connectionsClient.sendPayload(opponentEndpointId!!, Payload.fromBytes(choice.name.toByteArray(StandardCharsets.UTF_8)))
        setStatusText(getString(R.string.player_choice, choice.name))
        setGameChoicesEnabled(false)
    }

    private fun finishRound() {

        when {
            playerChoice?.beats(opponentChoice!!)!! -> {
                setStatusText(getString(R.string.win_message, playerChoice?.name, opponentChoice?.name))
                playerScore++
            }
            playerChoice == opponentChoice -> {
                setStatusText(getString(R.string.tie_message, playerChoice?.name))
            }
            else -> {
                setStatusText(getString(R.string.loss_message, playerChoice?.name, opponentChoice?.name))
                opponentScore++
            }
        }

        playerChoice = null
        opponentChoice = null
        updateScore(playerScore, opponentScore)

        // Ready for another round
        setGameChoicesEnabled(true)
    }

    /** Enables/disables buttons depending on the connection status.  */
    private fun setButtonState(connected: Boolean) {
        binding?.buttonFindOpponent?.isEnabled = true
        binding?.buttonFindOpponent?.visibility = if (connected) View.GONE else View.VISIBLE
        binding?.buttonDisconnect?.isEnabled = connected
        setGameChoicesEnabled(connected)
    }

    /** Enables/disables the rock, paper, and scissors buttons.  */
    private fun setGameChoicesEnabled(enabled: Boolean) {
        binding?.buttonRock?.isEnabled = enabled
        binding?.buttonPaper?.isEnabled = enabled
        binding?.buttonScissors?.isEnabled = enabled
    }

    /** Shows a status message to the user.  */
    private fun setStatusText(text: String) {
        binding?.textStatus?.text = text
    }

    /** Updates the opponent name on the UI.  */
    private fun setOpponentName(opponentName: String) {
        binding?.textOpponent?.text = getString(R.string.opponent_name, opponentName)
    }

    /** Updates the running score ticker.  */
    private fun updateScore(myScore: Int, opponentScore: Int) {
        binding?.textScore?.text = getString(R.string.game_score, myScore, opponentScore)
    }

    private fun setViewStyle() {
        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()
        if (isDarkMode) {
            binding?.buttonRock?.style(R.style.ButtonNormalDarkMode)
            binding?.buttonPaper?.style(R.style.ButtonNormalDarkMode)
            binding?.buttonScissors?.style(R.style.ButtonNormalDarkMode)
            binding?.buttonFindOpponent?.style(R.style.ButtonNormalDarkMode)
            binding?.buttonDisconnect?.style(R.style.ButtonNormalDarkMode)
        }
        else {
            binding?.buttonRock?.style(R.style.ButtonNormalLightMode)
            binding?.buttonPaper?.style(R.style.ButtonNormalLightMode)
            binding?.buttonScissors?.style(R.style.ButtonNormalLightMode)
            binding?.buttonFindOpponent?.style(R.style.ButtonNormalLightMode)
            binding?.buttonDisconnect?.style(R.style.ButtonNormalLightMode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}