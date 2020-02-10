package it.giovanni.kotlin.youtube.search

import android.os.Bundle
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.Provider
import com.google.android.youtube.player.YouTubePlayerFragment
import it.giovanni.kotlin.R
import it.giovanni.kotlin.youtube.YoutubeConnector
import kotlinx.android.synthetic.main.youtube_player_fragment_activity.*

@Suppress("DEPRECATION")
class YouTubePlayerFragmentActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    private var youTubePlayer: YouTubePlayer? = null

    companion object {
        var VIDEO_ID = ""
        val PLAYLIST_ID = "PLP7qPet500dfglA7FFTxBmB_snxCaMHDJ"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.youtube_player_fragment_activity)

        VIDEO_ID = intent.getStringExtra("VIDEO_ID")

        val youTubePlayerFragment = fragmentManager.findFragmentById(R.id.youtube_player_fragment) as YouTubePlayerFragment
        youTubePlayerFragment.initialize(YoutubeConnector.API_KEY, this)

        full_screen_button.setOnClickListener {
            youTubePlayer!!.setFullscreen(true)
        }
    }

    override fun onInitializationFailure(provider: Provider, result: YouTubeInitializationResult) {

        val requestErrorDialog = 1
        if (result.isUserRecoverableError)
            result.getErrorDialog(this, requestErrorDialog).show()
        else
            Toast.makeText(this, "onInitializationFailure(): $result", Toast.LENGTH_SHORT).show()
    }

    override fun onInitializationSuccess(provider: Provider, player: YouTubePlayer, restored: Boolean) {

        youTubePlayer = player
        Toast.makeText(applicationContext, "onInitializationSuccess()", Toast.LENGTH_SHORT).show()

        if (!restored) {
            player.cueVideo(VIDEO_ID)
            // player.cuePlaylist(PLAYLIST_ID)
        }
    }
}