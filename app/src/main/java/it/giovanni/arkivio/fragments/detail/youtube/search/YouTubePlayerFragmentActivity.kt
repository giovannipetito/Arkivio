package it.giovanni.arkivio.fragments.detail.youtube.search

import android.os.Bundle
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.Provider
import com.google.android.youtube.player.YouTubePlayerFragment
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.YoutubePlayerFragmentActivityBinding
import it.giovanni.arkivio.fragments.detail.youtube.YoutubeConnector

class YouTubePlayerFragmentActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    private var binding: YoutubePlayerFragmentActivityBinding? = null

    private var youTubePlayer: YouTubePlayer? = null

    companion object {
        var VIDEO_ID = ""
        // val PLAYLIST_ID = "PLP7qPet500dfglA7FFTxBmB_snxCaMHDJ"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = YoutubePlayerFragmentActivityBinding.inflate(layoutInflater)
        // setContentView(R.layout.youtube_player_fragment_activity)
        setContentView(binding?.root)

        VIDEO_ID = intent?.getStringExtra("VIDEO_ID")!!

        val youTubePlayerFragment = fragmentManager.findFragmentById(R.id.youtube_player_fragment) as YouTubePlayerFragment
        youTubePlayerFragment.initialize(YoutubeConnector.API_KEY, this)

        binding?.fullScreenButton?.setOnClickListener {
            youTubePlayer?.setFullscreen(true)
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}