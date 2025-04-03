package it.giovanni.arkivio.fragments.detail.exoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ima.ImaAdsLoader
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.ExoplayerLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class ExoPlayerFragment : DetailFragment() {

    private var layoutBinding: ExoplayerLayoutBinding? = null
    private val binding get() = layoutBinding

    private var player: ExoPlayer? = null
    private var adsLoader: ImaAdsLoader? = null

    private val contentUri = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
    // String adTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/omid_ad_samples&env=vp&gdfp_req=1&output=vast&sz=640x480&description_url=http%3A%2F%2Ftest_site.com%2Fhomepage&vpmute=0&vpa=0&vad_format=linear&url=http%3A%2F%2Ftest_site.com&vpos=preroll&unviewed_position_start=1&correlator=";
    private val adTagUrl: String = "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_ad_samples&sz=640x480&cust_params=sample_ct%3Dlinear&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator="

    companion object {
        val TAG: String = ExoPlayerFragment::class.java.name
    }

    override fun getTitle(): Int {
        return R.string.exoplayer_title
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
        // No-op
    }

    override fun onActionSearch(searchString: String) {
        // No-op
    }

    override fun onCreateBindingView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutBinding = ExoplayerLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Create (or reuse) your ImaAdsLoader
        //    If you want to share a single ImaAdsLoader across the app, do so at the application level
        adsLoader = ImaAdsLoader.Builder(requireContext()).build()

        // 2) Initialize ExoPlayer
        initializePlayer()
    }

    /**
     * ExoPlayer initialization with Media3
     */
    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(requireContext())
                .setLoadControl(
                    DefaultLoadControl
                        .Builder()
                        .build()
                )
                .build()

            // Bind the player to the layout
            binding?.playerView?.player = player
        }

        // 3) Provide the ViewGroup where IMA should render the ad UI
        //    Media3’s PlayerView has an overlay FrameLayout for ads:
        val adViewGroup = binding?.playerView?.overlayFrameLayout
        // or: val adViewGroup = binding.playerView.adViewGroup

        // Option A: Set a simple AdViewGroup
        // adsLoader?.setAdViewGroup(adViewGroup)

        // Option B (alternative): If you want more control, use AdViewProvider
        // imaAdsLoader?.setAdViewProvider(object : AdViewProvider {
        //     override fun getAdViewGroup(): ViewGroup = adViewGroup
        //     override fun getCompanionAdViewGroup(): ViewGroup? = null
        // })

        // 4) Attach the ads loader to the player. This is how IMA can insert ads at the correct time.
        adsLoader?.setPlayer(player)

        // 5) Build a MediaItem with an AdsConfiguration for your preroll ad
        val mediaItem = MediaItem.Builder()
            .setUri(contentUri)
            .setMimeType(MimeTypes.APPLICATION_MP4) // or whatever your content’s type is
            .setAdsConfiguration(
                MediaItem.AdsConfiguration.Builder(adTagUrl.toUri()).build()
            )
            .build()

        // 6) Set the media item and prepare
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true // Autoplay if desired
    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.playerView?.player = null // unbind the player from UI
        player?.release()
        player = null
        adsLoader?.release()
        adsLoader = null
        layoutBinding = null
    }
}