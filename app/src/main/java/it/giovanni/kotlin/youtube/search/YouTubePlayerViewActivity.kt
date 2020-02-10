package it.giovanni.kotlin.youtube.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import it.giovanni.kotlin.R
import it.giovanni.kotlin.youtube.YoutubeConnector
import kotlinx.android.synthetic.main.youtube_player_view_activity.*

class YouTubePlayerViewActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    private val RECOVERY_DIALOG_REQUEST = 1

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.youtube_player_view_activity)

        val playerView: YouTubePlayerView = findViewById(R.id.youtube_player_view)
        playerView.initialize(YoutubeConnector.API_KEY, this)

        player_title.text = intent.getStringExtra("VIDEO_TITLE")
        player_description.text = intent.getStringExtra("VIDEO_DESCRIPTION")
        player_id.text = intent.getStringExtra("VIDEO_ID")
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, restored: Boolean) {
        if (!restored) player.cueVideo(intent.getStringExtra("VIDEO_ID"))
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, result: YouTubeInitializationResult) {

        if (result.isUserRecoverableError)
            result.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        else {
            val errorMessage = String.format(getString(R.string.error_player), result.toString())
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action,
            getYouTubePlayerProvider()?.initialize(YoutubeConnector.API_KEY, this)
        }
    }

    private fun getYouTubePlayerProvider(): YouTubePlayer.Provider? {
        return findViewById<View>(R.id.youtube_player_view) as YouTubePlayerView
    }
}