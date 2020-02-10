package it.giovanni.kotlin.youtube

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.LinearLayout
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener
import com.google.android.youtube.player.YouTubePlayerView
import it.giovanni.kotlin.R

class FullScreenActivity : YouTubeBaseActivity(),
    YouTubePlayer.OnInitializedListener,
    View.OnClickListener,
    CompoundButton.OnCheckedChangeListener,
    OnFullscreenListener {

    companion object {
        private const val PORTRAIT_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }

    private var baseLayout: LinearLayout? = null
    private var playerView: YouTubePlayerView? = null
    private var player: YouTubePlayer? = null
    private var fullscreenButton: Button? = null
    private var checkbox: CompoundButton? = null
    private var otherViews: View? = null
    private var fullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.youtube_player_view_activity)

        baseLayout = findViewById(R.id.player_view_container)
        playerView = findViewById(R.id.youtube_player_view)
        fullscreenButton = findViewById(R.id.full_screen_button)
        checkbox = findViewById(R.id.landscape_fullscreen_checkbox)
        otherViews = findViewById(R.id.player_view_content)

        checkbox?.visibility = View.VISIBLE
        fullscreenButton?.visibility = View.VISIBLE

        checkbox?.setOnCheckedChangeListener(this)
        fullscreenButton?.setOnClickListener(this)
        playerView?.initialize(YoutubeConnector.API_KEY, this)

        layoutHandling()
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, wasRestored: Boolean) {
        this.player = player
        setControlsEnabled()
        // Specify that we want to handle fullscreen behavior ourselves.
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT)
        player.setOnFullscreenListener(this)
        if (!wasRestored) {
            player.cueVideo("avP5d16wEp0")
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, youTubeInitializationResult: YouTubeInitializationResult) {}

    override fun onClick(v: View) {
        player?.setFullscreen(!fullscreen)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {

        var controlFlags = player!!.fullscreenControlFlags
        if (isChecked) {
            requestedOrientation = PORTRAIT_ORIENTATION
            controlFlags = controlFlags or YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            controlFlags = controlFlags and YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE.inv()
        }
        player?.fullscreenControlFlags = controlFlags
    }

    private fun layoutHandling() {
        val playerParams = playerView!!.layoutParams as LinearLayout.LayoutParams
        if (fullscreen) {
            playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT
            otherViews?.visibility = View.GONE
        } else {
            otherViews?.visibility = View.VISIBLE
            val otherViewsParams = otherViews?.layoutParams
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                otherViewsParams?.width = 0
                playerParams.width = otherViewsParams?.width!!
                playerParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                otherViewsParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
                playerParams.weight = 1f
                baseLayout?.orientation = LinearLayout.HORIZONTAL
            } else {
                otherViewsParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
                playerParams.width = otherViewsParams?.width!!
                playerParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                playerParams.weight = 0f
                otherViewsParams?.height = 0
                baseLayout?.orientation = LinearLayout.VERTICAL
            }
            setControlsEnabled()
        }
    }

    private fun setControlsEnabled() {
        checkbox!!.isEnabled = player != null && resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        fullscreenButton!!.isEnabled = player != null
    }

    override fun onFullscreen(isFullscreen: Boolean) {
        fullscreen = isFullscreen
        layoutHandling()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        layoutHandling()
    }
}