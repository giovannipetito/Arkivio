package it.giovanni.arkivio.fragments.detail.exoplayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import it.giovanni.arkivio.R
import it.giovanni.arkivio.bean.advertising.*
import it.giovanni.arkivio.databinding.ExoplayerLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class ExoPlayerFragment: DetailFragment() {

    private var layoutBinding: ExoplayerLayoutBinding? = null
    private val binding get() = layoutBinding

    private var originalPosition: Int = 0

    private var player: ExoPlayer? = null

    private val analyticsListener = AnalyticsListener()

    // total timeline duration
    private var totalDurationMs: Long = C.TIME_UNSET

    // current position inside the timeline
    private var currentPositionMs: Long = 0L

    // advertising object
    private var advertising: Advertising? = null

    // current playback marker (used to determine the final position)
    private var finalPosition: Int? = null

    // current fragment is set to close. no further actions will be possible
    private var quitting: Boolean = false

    companion object {

        val TAG: String = ExoPlayerFragment::class.java.name

        private const val MIN_BUFFER_MS = 15000
        private const val MAX_BUFFER_MS = 20000
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
    }

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = ExoplayerLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quitting = false

        val order = 1
        val bumper = true

        val durationInSeconds = 100
        val identifier = "dcdc2ac0-f64a-4b26-babc-1df895c8cc90"
        // val url = "https://tvoosa-sez0402.sta.sctv.ch/sdash/ad-dcdc2ac0-f64a-4b26-babc-1df895c8cc90-OTT_JITP/index.mpd/Manifest?device=DASH-TV-static&request-id=454c679f0cd56a894ee789497f42e43560bc5630_1651237272"
        val url = "https://dash.akamaized.net/dash264/TestCases/1b/qualcomm/1/MultiRatePatched.mpd" // Test url
        val media = Media(durationInSeconds, identifier, url)

        val adIdentifier = "77de8589-81c5-4de1-bb54-d3eacdf52c31"
        val clientIdentifier = "GVNebRxuTO4pIgluVSET0Z89/I8="
        val events: ArrayList<Event> = ArrayList()

        val type1 = "complete"
        val uri1 = "https://ad.dev.sctv.ch/tracking?adServingId=77de8589-81c5-4de1-bb54-d3eacdf52c31&clientIdentifier=GVNebRxuTO4pIgluVSET0Z89%2FI8%3D&domain=TV2014&event=complete&rapUrl=aHR0cHM6Ly9hZC5hZHBvcnRhbC5jaC9UcmFja2luZz9hZHNlcnZpbmdpZD03N2RlODU4OS04MWM1LTRkZTEtYmI1NC1kM2VhY2RmNTJjMzEmQVBJS2V5PUI2NjM5NDdFLTJEOEQtNDBFMC05QTkxLUY2QUZBRDk0ODU4NCZldmVudD1jb21wbGV0ZQ=="
        val event1 = Event(type1, uri1)
        event1.offsetSeconds = 1

        val type2 = "error"
        val uri2 = "https://ad.dev.sctv.ch/tracking?adServingId=77de8589-81c5-4de1-bb54-d3eacdf52c31&clientIdentifier=GVNebRxuTO4pIgluVSET0Z89%2FI8%3D&domain=TV2014&event=error&errorCode=[ERRORCODE]&rapUrl=aHR0cHM6Ly9hZC5hZHBvcnRhbC5jaC9UcmFja2luZy9lcnJvcj9jPVtFUlJPUkNPREVdJmE9NzdkZTg1ODktODFjNS00ZGUxLWJiNTQtZDNlYWNkZjUyYzMx"
        val event2 = Event(type2, uri2)
        event2.offsetSeconds = 1

        events.add(event1)
        events.add(event2)

        val tracking = Tracking(adIdentifier, clientIdentifier, events)

        val advertisingItem = AdvertisingItem(order, media, tracking, bumper)
        val items: ArrayList<AdvertisingItem> = ArrayList()

        items.add(advertisingItem)

        advertising = Advertising(items)

        finalPosition = 100
        originalPosition = 1
    }

    override fun onResume() {
        super.onResume()

        if (player == null) {
            initializePlayer()
            onReady()
            load(advertising!!) // Start loading the media source items into the player.
        }
    }

    /**
     * ExoPlayer initialization
     */
    private fun initializePlayer() {
        if (!isAdded || player != null) {
            return
        }

        val trackSelector = DefaultTrackSelector(requireContext()).apply {
            setParameters(buildUponParameters())
        }

        player = ExoPlayer.Builder(requireContext())
            .setLoadControl(
                DefaultLoadControl
                    .Builder()
                    .setBufferDurationsMs(
                        MIN_BUFFER_MS, // 15000 ms
                        MAX_BUFFER_MS, // 20000 ms
                        DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS, // 2500 ms
                        DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                    )
                    .setTargetBufferBytes(DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES)
                    .build()
            )
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                binding?.playerView?.let { playerView ->
                    playerView.player = exoPlayer
                    playerView.visibility = View.INVISIBLE
                    playerView.videoSurfaceView?.visibility = View.INVISIBLE
                }

                exoPlayer.playWhenReady = false
                exoPlayer.seekTo(0, 0)
                exoPlayer.addAnalyticsListener(analyticsListener)
            }
    }

    /**
     * Everything is setup and ready. the alt clip will start
     */
    private fun onReady() {
        if (!isAdded) return

        binding?.let { viewBinding ->
            viewBinding.loader.delayedProgressBar.hide()
            viewBinding.playerView.visibility = View.VISIBLE
            viewBinding.playerView.videoSurfaceView?.visibility = View.VISIBLE
        }
        play()
    }

    /**
     * Start the ExoPlayer playback. This means everything is in place and we're ready to play the videos
     */
    private fun play() {
        if (isAdded) {
            player?.play()
        } else {
            Log.i(TAG, "added=$isAdded, player.playbackState=${player?.playbackState}")
            quit("Something is wrong!")
        }
    }

    /**
     * Stop everything and remove the Fragment
     */
    private fun quit(reason: String? = null) {
        // avoid multiple calls
        if (quitting) return

        quitting = true

        // stop player and release it
        player?.stop()
        releasePlayer()
    }

    private fun load(advertising: Advertising) {
        if (!isAdded) {
            return
        }

        player?.let { exoPlayer ->
            val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(requireContext())
            val mediaSource = ConcatenatingMediaSource()
            var total = 0L
            advertising.items.forEach { item ->
                val media = item.media as Media
                Log.i(TAG, "adding media item: ${media.url} [bumper: ${item.bumper}, duration: ${media.durationInSeconds}]")
                mediaSource.addMediaSource(DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(media.url!!)))
                total += media.durationInSeconds.toLong()
            }

            totalDurationMs = total * 5000
            Log.i(TAG, "total duration = $totalDurationMs")
            exoPlayer.addMediaSource(mediaSource)
            exoPlayer.prepare()
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    /**
     * Release the ExoPlayer and set it to null
     */
    private fun releasePlayer() {
        player?.run {
            removeAnalyticsListener(analyticsListener)
            release()
        }
        player = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }

    private inner class AnalyticsListener : com.google.android.exoplayer2.analytics.AnalyticsListener {
        override fun onPositionDiscontinuity(
            eventTime: com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime,
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(eventTime, oldPosition, newPosition, reason)
            currentPositionMs += oldPosition.positionMs
        }
    }
}