package it.giovanni.arkivio.fragments.detail.youtube.videolist

import android.os.Bundle
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import it.giovanni.arkivio.fragments.detail.youtube.YoutubeConnector

class VideoFragment : YouTubePlayerFragment(), YouTubePlayer.OnInitializedListener {

    private var player: YouTubePlayer? = null
    private var videoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(YoutubeConnector.API_KEY, this)
    }

    override fun onDestroy() {
        player?.release()
        super.onDestroy()
    }

    fun setVideoId(videoId: String?) {
        if (videoId != null && videoId != this.videoId) {
            this.videoId = videoId
            player?.cueVideo(videoId)
        }
    }

    fun pause() {
        player?.pause()
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, restored: Boolean) {
        this.player = player
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT)
        player.setOnFullscreenListener(activity as VideoListActivity)
        if (!restored && videoId != null)
            player.cueVideo(videoId)
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, result: YouTubeInitializationResult) {
        player = null
    }
}