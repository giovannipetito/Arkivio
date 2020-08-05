package it.giovanni.arkivio.fragments.detail.youtube

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView.OnEditorActionListener
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.*
import com.google.android.youtube.player.YouTubePlayerView
import it.giovanni.arkivio.App.Companion.context
import it.giovanni.arkivio.R
import kotlinx.android.synthetic.main.player_controls_activity.*
import kotlinx.android.synthetic.main.player_controls_board.*

class PlayerControlsActivity : YouTubeBaseActivity(),
    OnInitializedListener,
    View.OnClickListener,
    OnEditorActionListener,
    CompoundButton.OnCheckedChangeListener,
    OnItemSelectedListener {

    private var player: YouTubePlayer? = null
    private var selectedPosition = 0
    private var selectedId: String? = null
    private lateinit var youTubePlayerView: YouTubePlayerView
    private lateinit var eventLog: StringBuilder
    private lateinit var palyerRadioGroup: RadioGroup
    private lateinit var videoAdapter: ArrayAdapter<ListEntry>
    private lateinit var playlistEventListener: MyPlaylistEventListener
    private lateinit var playerStateChangeListener: MyPlayerStateChangeListener
    private lateinit var playbackEventListener: MyPlaybackEventListener

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_controls_activity)

        youTubePlayerView = findViewById(R.id.youtube_player_view)
        palyerRadioGroup = findViewById(R.id.player_radio_group)
        (findViewById<View>(R.id.style_default) as RadioButton).setOnCheckedChangeListener(this)
        (findViewById<View>(R.id.style_minimal) as RadioButton).setOnCheckedChangeListener(this)
        (findViewById<View>(R.id.style_chromeless) as RadioButton).setOnCheckedChangeListener(this)
        eventLog = StringBuilder()
        videoAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, ENTRIES)
        videoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_video.onItemSelectedListener = this
        spinner_video.adapter = videoAdapter
        play_button.setOnClickListener(this)
        pause_button.setOnClickListener(this)
        skip_to_text.setOnEditorActionListener(this)
        youTubePlayerView.initialize(YoutubeConnector.API_KEY, this)
        playlistEventListener = MyPlaylistEventListener()
        playerStateChangeListener = MyPlayerStateChangeListener()
        playbackEventListener = MyPlaybackEventListener()
        setControlsEnabled(false)
    }

    override fun onInitializationSuccess(provider: Provider, player: YouTubePlayer, restored: Boolean) {
        this.player = player
        player.setPlaylistEventListener(playlistEventListener)
        player.setPlayerStateChangeListener(playerStateChangeListener)
        player.setPlaybackEventListener(playbackEventListener)
        if (!restored) {
            playVideoAtSelection()
        }
        setControlsEnabled(true)
    }

    override fun onInitializationFailure(p0: Provider?, p1: YouTubeInitializationResult?) {}

    private fun playVideoAtSelection() {
        val selectedEntry = videoAdapter.getItem(selectedPosition)
        if (selectedEntry != null) {
            if (selectedEntry.id != selectedId && player != null) {
                selectedId = selectedEntry.id
                if (selectedEntry.isPlaylist)
                    player!!.cuePlaylist(selectedEntry.id)
                else
                    player!!.cueVideo(selectedEntry.id)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedPosition = position
        playVideoAtSelection()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onClick(v: View) {
        if (v === play_button)
            player!!.play()
        else if (v === pause_button)
            player!!.pause()
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (v === skip_to_text) {
            val skipToSecs = parseInt(skip_to_text.text.toString(), 0)
            player!!.seekToMillis(skipToSecs * 1000)
            hideSoftKeyboard()
            return true
        }
        return false
    }

    private fun hideSoftKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked && player != null) {
            when (buttonView.id) {
                R.id.style_default -> player!!.setPlayerStyle(PlayerStyle.DEFAULT)
                R.id.style_minimal -> player!!.setPlayerStyle(PlayerStyle.MINIMAL)
                R.id.style_chromeless -> player!!.setPlayerStyle(PlayerStyle.CHROMELESS)
            }
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putString(KEY_CURRENTLY_SELECTED_ID, selectedId)
    }

    override fun onRestoreInstanceState(state: Bundle) {
        super.onRestoreInstanceState(state)
        selectedId = state.getString(KEY_CURRENTLY_SELECTED_ID)
    }

    private fun updateText() {
        state_text.text = String.format("Current state: %s %s %s",
            playerStateChangeListener.playerState,
            playbackEventListener.playbackState,
            playbackEventListener.bufferingState
        )
    }

    private fun log(message: String) {
        eventLog.append(message + "\n")
        event_log.text = eventLog
    }

    private fun setControlsEnabled(enabled: Boolean) {
        play_button.isEnabled = enabled
        pause_button.isEnabled = enabled
        skip_to_text.isEnabled = enabled
        spinner_video.isEnabled = enabled
        for (i in 0 until palyerRadioGroup.childCount) {
            palyerRadioGroup.getChildAt(i).isEnabled = enabled
        }
    }

    private fun formatTime(millis: Int): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return (if (hours == 0) "" else "$hours:") + String.format("%02d:%02d", minutes % 60, seconds % 60)
    }

    private val timesText: String
        get() {
            val currentTimeMillis = player!!.currentTimeMillis
            val durationMillis = player!!.durationMillis
            return String.format("(%s/%s)", formatTime(currentTimeMillis), formatTime(durationMillis))
        }

    inner class MyPlaylistEventListener : PlaylistEventListener {
        override fun onNext() {
            log("NEXT VIDEO")
        }

        override fun onPrevious() {
            log("PREVIOUS VIDEO")
        }

        override fun onPlaylistEnded() {
            log("PLAYLIST ENDED")
        }
    }

    inner class MyPlaybackEventListener : PlaybackEventListener {

        var playbackState = "NOT_PLAYING"
        var bufferingState = ""

        override fun onPlaying() {
            playbackState = "PLAYING"
            updateText()
            log("PLAYING $timesText")
        }

        override fun onBuffering(isBuffering: Boolean) {
            bufferingState = if (isBuffering) "(BUFFERING)" else ""
            updateText()
            log("\t" + (if (isBuffering) "BUFFERING " else "NOT BUFFERING ") + timesText)
        }

        override fun onStopped() {
            playbackState = "STOPPED"
            updateText()
            log("STOPPED")
        }

        override fun onPaused() {
            playbackState = "PAUSED"
            updateText()
            log("PAUSED $timesText")
        }

        override fun onSeekTo(endPositionMillis: Int) {
            log(String.format("\tSEEKTO: (%s/%s)", formatTime(endPositionMillis), formatTime(player!!.durationMillis)))
        }
    }

    inner class MyPlayerStateChangeListener : PlayerStateChangeListener {
        var playerState = "UNINITIALIZED"
        override fun onLoading() {
            playerState = "LOADING"
            updateText()
            log(playerState)
        }

        override fun onLoaded(videoId: String) {
            playerState = String.format("LOADED %s", videoId)
            updateText()
            log(playerState)
        }

        override fun onAdStarted() {
            playerState = "AD_STARTED"
            updateText()
            log(playerState)
        }

        override fun onVideoStarted() {
            playerState = "VIDEO_STARTED"
            updateText()
            log(playerState)
        }

        override fun onVideoEnded() {
            playerState = "VIDEO_ENDED"
            updateText()
            log(playerState)
        }

        override fun onError(reason: ErrorReason) {
            playerState = "ERROR ($reason)"
            if (reason == ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
                player = null
                setControlsEnabled(false)
            }
            updateText()
            log(playerState)
        }
    }

    class ListEntry(val title: String, val id: String, val isPlaylist: Boolean) {

        override fun toString(): String {
            return title
        }
    }

    companion object {

        private val ENTRIES = arrayOf(
            ListEntry("Androidify App", "irH3OSOskcE", false),
            ListEntry("Chrome Speed Tests", "nCgQDjiotG0", false),
            ListEntry("Playlist: Google I/O 2012", "PL56D792A831D0C362", true)
        )

        private const val KEY_CURRENTLY_SELECTED_ID = "currentlySelectedId"

        private fun parseInt(intString: String?, defaultValue: Int): Int {
            return try {
                if (intString != null) Integer.valueOf(intString) else defaultValue
            } catch (e: NumberFormatException) {
                defaultValue
            }
        }
    }
}