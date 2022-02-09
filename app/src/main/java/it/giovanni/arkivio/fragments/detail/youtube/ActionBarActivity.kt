package it.giovanni.arkivio.fragments.detail.youtube

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener
import com.google.android.youtube.player.YouTubePlayerFragment
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.YoutubePlayerFragmentActivityBinding

class ActionBarActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener, OnFullscreenListener {

    private var playerFragment: YouTubePlayerFragment? = null

    private var layoutBinding: YoutubePlayerFragmentActivityBinding? = null
    val binding get() = layoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layoutBinding = YoutubePlayerFragmentActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        playerFragment = fragmentManager.findFragmentById(R.id.youtube_player_fragment) as YouTubePlayerFragment
        playerFragment?.initialize(YoutubeConnector.API_KEY, this)

        actionBar?.setBackgroundDrawable(ColorDrawable(-0x56000000))
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, wasRestored: Boolean) {
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT)
        player.setOnFullscreenListener(this)
        if (!wasRestored)
            player.cueVideo("9c6W4CCU9M4")
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, youTubeInitializationResult: YouTubeInitializationResult) {}

    override fun onFullscreen(fullscreen: Boolean) {
        val playerParams = playerFragment?.view?.layoutParams
        if (fullscreen) {
            playerParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
            playerParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            playerParams?.width = 0
            playerParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        layoutBinding = null
    }
}