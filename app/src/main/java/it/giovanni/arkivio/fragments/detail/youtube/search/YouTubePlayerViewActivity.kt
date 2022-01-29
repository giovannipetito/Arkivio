package it.giovanni.arkivio.fragments.detail.youtube.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.YoutubePlayerViewActivityBinding
import it.giovanni.arkivio.fragments.detail.youtube.YoutubeConnector

class YouTubePlayerViewActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    private var binding: YoutubePlayerViewActivityBinding? = null

    private val recoveryDialogRequest = 1

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        binding = YoutubePlayerViewActivityBinding.inflate(layoutInflater)
        // setContentView(R.layout.youtube_player_view_activity)
        setContentView(binding?.root)

        val playerView: YouTubePlayerView = findViewById(R.id.youtube_player_view)
        playerView.initialize(YoutubeConnector.API_KEY, this)

        binding?.playerTitle?.text = intent.getStringExtra("VIDEO_TITLE")
        binding?.playerDescription?.text = intent.getStringExtra("VIDEO_DESCRIPTION")
        binding?.playerId?.text = intent.getStringExtra("VIDEO_ID")
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, restored: Boolean) {
        if (!restored) player.cueVideo(intent.getStringExtra("VIDEO_ID"))
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, result: YouTubeInitializationResult) {

        if (result.isUserRecoverableError)
            result.getErrorDialog(this, recoveryDialogRequest).show()
        else {
            val errorMessage = String.format(getString(R.string.error_player), result.toString())
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == recoveryDialogRequest) {
            // Retry initialization if user performed a recovery action,
            getYouTubePlayerProvider().initialize(YoutubeConnector.API_KEY, this)
        }
    }

    private fun getYouTubePlayerProvider(): YouTubePlayer.Provider {
        return findViewById<View>(R.id.youtube_player_view) as YouTubePlayerView
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}