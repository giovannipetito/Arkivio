package it.giovanni.kotlin.fragments.detail.youtube.videowall

import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.youtube.player.*
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle
import com.google.android.youtube.player.YouTubeThumbnailLoader.OnThumbnailLoadedListener
import it.giovanni.kotlin.App.Companion.context
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.detail.youtube.YoutubeConnector
import kotlin.math.floor
import kotlin.math.min

@TargetApi(11)
class VideoWallActivity : Activity(), FlippingView.Listener,
    YouTubePlayer.OnInitializedListener, YouTubeThumbnailView.OnInitializedListener {

    lateinit var imageWallView: ImageWallView
    lateinit var flipDelayHandler: Handler
    lateinit var flippingView: FlippingView
    lateinit var thumbnailView: YouTubeThumbnailView
    lateinit var playerFragment: YouTubePlayerFragment
    lateinit var playerView: View
    private var thumbnailLoader: YouTubeThumbnailLoader? = null
    private var player: YouTubePlayer? = null
    private var errorDialog: Dialog? = null
    private var flippingCol = 0
    private var flippingRow = 0
    private var videoCol = 0
    private var videoRow = 0
    private var nextThumbnailLoaded = false
    private var activityResumed = false
    private var state: State? = null

    private enum class State {
        VIDEO_CUED,
        VIDEO_ENDED,
        UNINITIALIZED,
        VIDEO_LOADING,
        VIDEO_PLAYING,
        VIDEO_FLIPPED_OUT,
        LOADING_THUMBNAILS,
        VIDEO_BEING_FLIPPED_OUT
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        state = State.UNINITIALIZED
        val viewFrame: ViewGroup = FrameLayout(context)
        val displayMetrics = resources.displayMetrics
        val maxAllowedNumberOfRows = floor(displayMetrics.heightPixels / displayMetrics.density / PLAYER_VIEW_MINIMUM_HEIGHT_DP.toDouble()).toInt()
        val numberOfRows = min(maxAllowedNumberOfRows, MAX_NUMBER_OF_ROWS_WANTED)
        val interImagePaddingPx = displayMetrics.density.toInt() * INTER_IMAGE_PADDING_DP
        val imageHeight = displayMetrics.heightPixels / numberOfRows - interImagePaddingPx
        val imageWidth = (imageHeight * THUMBNAIL_ASPECT_RATIO).toInt()

        imageWallView = ImageWallView(context, imageWidth, imageHeight, interImagePaddingPx)
        viewFrame.addView(imageWallView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        thumbnailView = YouTubeThumbnailView(this)
        thumbnailView.initialize(YoutubeConnector.API_KEY, this)
        flippingView = FlippingView(context, this, imageWidth, imageHeight)
        flippingView.setFlipDuration(INITIAL_FLIP_DURATION_MILLIS)
        viewFrame.addView(flippingView, imageWidth, imageHeight)
        playerView = FrameLayout(context)
        playerView.id = R.id.player_view
        playerView.visibility = View.INVISIBLE
        viewFrame.addView(playerView, imageWidth, imageHeight)
        playerFragment = YouTubePlayerFragment.newInstance()
        playerFragment.initialize(YoutubeConnector.API_KEY, this)
        fragmentManager.beginTransaction().add(R.id.player_view, playerFragment).commit()
        flipDelayHandler = FlipDelayHandler()
        setContentView(viewFrame)
    }

    override fun onInitializationSuccess(thumbnailView: YouTubeThumbnailView, thumbnailLoader: YouTubeThumbnailLoader) {
        this.thumbnailLoader = thumbnailLoader
        thumbnailLoader.setOnThumbnailLoadedListener(ThumbnailListener())
        maybeStartDemo()
    }

    override fun onInitializationFailure(thumbnailView: YouTubeThumbnailView, errorReason: YouTubeInitializationResult) {
        if (errorReason.isUserRecoverableError) {
            if (errorDialog == null || !errorDialog?.isShowing!!) {
                errorDialog = errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST)
                errorDialog?.show()
            }
        } else {
            val errorMessage = String.format(getString(R.string.error_thumbnail_view), errorReason.toString())
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, wasResumed: Boolean) {
        this@VideoWallActivity.player = player
        player.setPlayerStyle(PlayerStyle.CHROMELESS)
        player.setPlayerStateChangeListener(VideoListener())
        maybeStartDemo()
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, errorReason: YouTubeInitializationResult) {
        if (errorReason.isUserRecoverableError) {
            if (errorDialog == null || !errorDialog?.isShowing!!) {
                errorDialog = errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST)
                errorDialog?.show()
            }
        } else {
            val errorMessage = String.format(getString(R.string.error_player), errorReason.toString())
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun maybeStartDemo() {
        if (activityResumed && player != null && thumbnailLoader != null && state == State.UNINITIALIZED) {
            thumbnailLoader?.setPlaylist(PLAYLIST_ID) // loading the first thumbnail will kick off demo
            state = State.LOADING_THUMBNAILS
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) { // Retry initialization if user performed a recovery action
            if (errorDialog != null && errorDialog?.isShowing!!) {
                errorDialog?.dismiss()
            }
            errorDialog = null
            playerFragment.initialize(YoutubeConnector.API_KEY, this)
            thumbnailView.initialize(YoutubeConnector.API_KEY, this)
        }
    }

    override fun onResume() {
        super.onResume()
        activityResumed = true
        if (thumbnailLoader != null && player != null) {
            when (state) {
                State.UNINITIALIZED -> {
                    maybeStartDemo()
                }
                State.LOADING_THUMBNAILS -> {
                    loadNextThumbnail()
                }
                else -> {
                    if (state == State.VIDEO_PLAYING) {
                        player?.play()
                    }
                    flipDelayHandler.sendEmptyMessageDelayed(0, FLIP_DURATION_MILLIS.toLong())
                }
            }
        }
    }

    override fun onPause() {
        flipDelayHandler.removeCallbacksAndMessages(null)
        activityResumed = false
        super.onPause()
    }

    override fun onDestroy() {
        if (thumbnailLoader != null) {
            thumbnailLoader?.release()
        }
        super.onDestroy()
    }

    private fun flipNext() {
        if (!nextThumbnailLoaded || state == State.VIDEO_LOADING) {
            return
        }
        if (state == State.VIDEO_ENDED) {
            flippingCol = videoCol
            flippingRow = videoRow
            state = State.VIDEO_BEING_FLIPPED_OUT
        } else {
            val nextTarget = imageWallView.nextLoadTarget
            flippingCol = nextTarget.first
            flippingRow = nextTarget.second
        }
        flippingView.x = imageWallView.getXPosition(flippingCol, flippingRow).toFloat()
        flippingView.y = imageWallView.getYPosition(flippingCol, flippingRow).toFloat()
        flippingView.setFlipInDrawable(thumbnailView.drawable)
        flippingView.setFlipOutDrawable(imageWallView.getImageDrawable(flippingCol, flippingRow))
        imageWallView.setImageDrawable(flippingCol, flippingRow, thumbnailView.drawable)
        imageWallView.hideImage(flippingCol, flippingRow)
        flippingView.visibility = View.VISIBLE
        flippingView.flip()
    }

    override fun onFlipped(view: FlippingView) {
        imageWallView.showImage(flippingCol, flippingRow)
        flippingView.visibility = View.INVISIBLE
        if (activityResumed) {
            loadNextThumbnail()
            if (state == State.VIDEO_BEING_FLIPPED_OUT) {
                state = State.VIDEO_FLIPPED_OUT
            } else if (state == State.VIDEO_CUED) {
                videoCol = flippingCol
                videoRow = flippingRow
                playerView.x = imageWallView.getXPosition(flippingCol, flippingRow).toFloat()
                playerView.y = imageWallView.getYPosition(flippingCol, flippingRow).toFloat()
                imageWallView.hideImage(flippingCol, flippingRow)
                playerView.visibility = View.VISIBLE
                player?.play()
                state = State.VIDEO_PLAYING
            } else if (state == State.LOADING_THUMBNAILS && imageWallView.allImagesLoaded()) {
                state = State.VIDEO_FLIPPED_OUT // trigger flip in of an initial video
                flippingView.setFlipDuration(FLIP_DURATION_MILLIS)
                flipDelayHandler.sendEmptyMessage(0)
            }
        }
    }

    private fun loadNextThumbnail() {
        nextThumbnailLoaded = false
        if (thumbnailLoader?.hasNext()!!) {
            thumbnailLoader?.next()
        } else {
            thumbnailLoader?.first()
        }
    }

    /**
     * A handler that periodically flips an element on the video wall.
     */
    private inner class FlipDelayHandler : Handler() {
        override fun handleMessage(msg: Message) {
            flipNext()
            sendEmptyMessageDelayed(0, FLIP_PERIOD_MILLIS.toLong())
        }
    }

    /**
     * An internal listener which listens to thumbnail loading events from the
     * [YouTubeThumbnailView].
     */
    private inner class ThumbnailListener : OnThumbnailLoadedListener {

        override fun onThumbnailLoaded(thumbnail: YouTubeThumbnailView, videoId: String) {
            nextThumbnailLoaded = true
            if (activityResumed) {
                if (state == State.LOADING_THUMBNAILS) {
                    flipNext()
                } else if (state == State.VIDEO_FLIPPED_OUT) { // load player with the video of the next thumbnail being flipped in
                    state = State.VIDEO_LOADING
                    player?.cueVideo(videoId)
                }
            }
        }

        override fun onThumbnailError(thumbnail: YouTubeThumbnailView, reason: YouTubeThumbnailLoader.ErrorReason) {
            loadNextThumbnail()
        }
    }

    private inner class VideoListener : PlayerStateChangeListener {

        override fun onLoaded(videoId: String) {
            state = State.VIDEO_CUED
        }

        override fun onVideoEnded() {
            state = State.VIDEO_ENDED
            imageWallView.showImage(videoCol, videoRow)
            playerView.visibility = View.INVISIBLE
        }

        override fun onError(errorReason: YouTubePlayer.ErrorReason) {
            if (errorReason == YouTubePlayer.ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) { // player has encountered an unrecoverable error - stop the demo
                flipDelayHandler.removeCallbacksAndMessages(null)
                state = State.UNINITIALIZED
                thumbnailLoader?.release()
                thumbnailLoader = null
                player = null
            } else {
                state = State.VIDEO_ENDED
            }
        }

        // ignored callbacks
        override fun onVideoStarted() {}
        override fun onAdStarted() {}
        override fun onLoading() {}
    }

    companion object {
        private const val RECOVERY_DIALOG_REQUEST = 1
        /** The player view cannot be smaller than 110 pixels high.  */
        private const val PLAYER_VIEW_MINIMUM_HEIGHT_DP = 110f
        private const val MAX_NUMBER_OF_ROWS_WANTED = 4
        // Example playlist from which videos are displayed on the video wall
        private const val PLAYLIST_ID = "ECAE6B03CA849AD332"
        private const val INTER_IMAGE_PADDING_DP = 5
        // YouTube thumbnails have a 16 / 9 aspect ratio
        private const val THUMBNAIL_ASPECT_RATIO = 16 / 9.0
        private const val INITIAL_FLIP_DURATION_MILLIS = 100
        private const val FLIP_DURATION_MILLIS = 500
        private const val FLIP_PERIOD_MILLIS = 2000
    }
}