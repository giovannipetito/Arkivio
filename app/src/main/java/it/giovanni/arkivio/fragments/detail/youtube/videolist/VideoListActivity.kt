@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.fragments.detail.youtube.videolist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import com.google.android.youtube.player.*
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener
import it.giovanni.arkivio.R

@TargetApi(13)
class VideoListActivity : Activity(), OnFullscreenListener {

    private var listFragment: VideoListFragment? = null
    private var videoFragment: VideoFragment? = null
    private var videoBox: View? = null
    private var closeButton: View? = null
    private var isFullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_list_activity)
        listFragment = fragmentManager.findFragmentById(R.id.list_fragment) as VideoListFragment
        videoFragment = fragmentManager.findFragmentById(R.id.video_fragment_container) as VideoFragment
        videoBox = findViewById(R.id.video_box)
        closeButton = findViewById(R.id.close_button)
        videoBox?.visibility = View.INVISIBLE
        layout()
        checkYouTubeApi()

        closeButton?.setOnClickListener {
            listFragment?.listView?.clearChoices()
            listFragment?.listView?.requestLayout()
            videoFragment?.pause()
            val animator = videoBox?.
                animate()?.
                translationYBy(videoBox?.height?.toFloat()!!)?.
                setDuration(ANIMATION_DURATION_MILLIS.toLong())!!
            runOnAnimationEnd(animator) { videoBox!!.visibility = View.INVISIBLE }
        }
    }

    private fun checkYouTubeApi() {
        val errorReason = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this)
        if (errorReason.isUserRecoverableError) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else if (errorReason != YouTubeInitializationResult.SUCCESS) {
            val errorMessage = String.format(getString(R.string.error_player), errorReason.toString())
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) { // Recreate the activity if user performed a recovery action
            recreate()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        layout()
    }

    override fun onFullscreen(isFullscreen: Boolean) {
        this.isFullscreen = isFullscreen
        layout()
    }

    private fun layout() {
        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        closeButton!!.visibility = if (isPortrait) View.VISIBLE else View.GONE
        when {
            isFullscreen -> {
                videoBox!!.translationY = 0f // Reset any translation that was applied in portrait.
                setLayoutSize(
                    videoFragment?.view!!,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setLayoutSizeAndGravity(
                    videoBox,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.TOP or Gravity.START
                )
            }
            isPortrait -> {
                setLayoutSize(
                    listFragment?.view!!,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setLayoutSize(
                    videoFragment?.view!!,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setLayoutSizeAndGravity(
                    videoBox,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM
                )
            }
            else -> {
                videoBox!!.translationY = 0f // Reset any translation that was applied in portrait.
                val screenWidth = dpToPx(resources.configuration.screenWidthDp)
                setLayoutSize(
                    listFragment?.view!!,
                    screenWidth / 4,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                val videoWidth =
                    screenWidth - screenWidth / 4 - dpToPx(LANDSCAPE_VIDEO_PADDING_DP)
                setLayoutSize(
                    videoFragment?.view!!,
                    videoWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setLayoutSizeAndGravity(
                    videoBox, videoWidth, ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.END or Gravity.CENTER_VERTICAL
                )
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(16)
    private fun runOnAnimationEnd(animator: ViewPropertyAnimator, runnable: Runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            animator.withEndAction(runnable)
        } else {
            animator.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    runnable.run()
                }
            })
        }
    }

    class VideoEntry internal constructor(val text: String, val videoId: String)

    // Utility methods for layouting.
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }

    companion object {

        const val ANIMATION_DURATION_MILLIS = 300
        const val LANDSCAPE_VIDEO_PADDING_DP = 5
        const val RECOVERY_DIALOG_REQUEST = 1

        private fun setLayoutSize(view: View, width: Int, height: Int) {

            val params = view.layoutParams
            params.width = width
            params.height = height
            view.layoutParams = params
        }

        private fun setLayoutSizeAndGravity(view: View?, width: Int, height: Int, gravity: Int) {

            val params = view!!.layoutParams as FrameLayout.LayoutParams
            params.width = width
            params.height = height
            params.gravity = gravity
            view.layoutParams = params
        }
    }
}