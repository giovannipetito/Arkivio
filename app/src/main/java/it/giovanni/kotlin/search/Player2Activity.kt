package it.giovanni.kotlin.search

import android.content.Intent.getIntent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener
import com.google.android.youtube.player.YouTubePlayer.Provider
import com.google.android.youtube.player.YouTubePlayerFragment
import it.giovanni.kotlin.R

class Player2Activity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    private var youTubePlayer: YouTubePlayer? = null
    private var textVideoLog: TextView? = null
    private var myPlayerStateChangeListener: MyPlayerStateChangeListener? = null
    private var myPlaybackEventListener: MyPlaybackEventListener? = null

    internal var log = ""

    companion object {
        var VIDEO_ID = ""
        val PLAYLIST_ID = "PLP7qPet500dfglA7FFTxBmB_snxCaMHDJ"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_2)

        textVideoLog = findViewById(R.id.video_log)
        val fullScreenButton = findViewById<Button>(R.id.full_screen_button)

        VIDEO_ID = intent.getStringExtra("VIDEO_ID")

        val youTubePlayerFragment = fragmentManager.findFragmentById(R.id.youtube_player_fragment) as YouTubePlayerFragment
        youTubePlayerFragment.initialize(YoutubeConnector.API_KEY, this)

        myPlayerStateChangeListener = MyPlayerStateChangeListener()
        myPlaybackEventListener = MyPlaybackEventListener()

        fullScreenButton.setOnClickListener {
            youTubePlayer!!.setFullscreen(true)
        }
    }

    override fun onInitializationFailure(provider: Provider, result: YouTubeInitializationResult) {

        val rqsErrorDialog = 1
        if (result.isUserRecoverableError)
            result.getErrorDialog(this, rqsErrorDialog).show()
        else
            Toast.makeText(this, "onInitializationFailure(): $result", Toast.LENGTH_SHORT).show()
    }

    override fun onInitializationSuccess(provider: Provider, player: YouTubePlayer, restored: Boolean) {

        youTubePlayer = player
        Toast.makeText(applicationContext, "onInitializationSuccess()", Toast.LENGTH_SHORT).show()

        youTubePlayer!!.setPlayerStateChangeListener(myPlayerStateChangeListener)
        youTubePlayer!!.setPlaybackEventListener(myPlaybackEventListener)

        if (!restored) {
            player.cueVideo(VIDEO_ID)
            // player.cuePlaylist(PLAYLIST_ID)
        }
    }

    private inner class MyPlayerStateChangeListener : PlayerStateChangeListener {

        private fun updateLog(prompt: String) {
            log += "MyPlayerStateChangeListener\n$prompt\n\n"
            textVideoLog!!.text = log
        }

        override fun onAdStarted() {
            updateLog("onAdStarted()")
        }

        override fun onError(arg0: YouTubePlayer.ErrorReason) {
            updateLog("onError(): $arg0")
        }

        override fun onLoaded(arg0: String) {
            updateLog("onLoaded(): $arg0")
        }

        override fun onLoading() {
            updateLog("onLoading()")
        }

        override fun onVideoEnded() {
            updateLog("onVideoEnded()")
        }

        override fun onVideoStarted() {
            updateLog("onVideoStarted()")
        }
    }

    private inner class MyPlaybackEventListener : PlaybackEventListener {

        private fun updateLog(prompt: String) {
            log += "MyPlaybackEventListener\n$prompt\n\n"
            textVideoLog!!.text = log
        }

        override fun onBuffering(arg0: Boolean) {
            updateLog("onBuffering(): $arg0")
        }

        override fun onPaused() {
            updateLog("onPaused()")
        }

        override fun onPlaying() {
            updateLog("onPlaying()")
        }

        override fun onSeekTo(arg0: Int) {
            updateLog("onSeekTo(): $arg0")
        }

        override fun onStopped() {
            updateLog("onStopped()")
        }
    }
}