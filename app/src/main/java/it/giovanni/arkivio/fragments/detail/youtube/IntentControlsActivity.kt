package it.giovanni.arkivio.fragments.detail.youtube

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeStandalonePlayer
import it.giovanni.arkivio.R
import kotlinx.android.synthetic.main.intent_controls_activity.*
import java.util.*

class IntentControlsActivity : Activity(), View.OnClickListener {

    companion object {

        private const val REQ_START_STANDALONE_PLAYER = 1
        private const val REQ_RESOLVE_SERVICE_MISSING = 2
        private const val VIDEO_ID = "BRSpqZOPEas"
        private const val PLAYLIST_ID = "PLPXvXoGcCrLdblRv-1FEA_mbla4qgqC6w"
        private val VIDEO_LIST_ID = ArrayList(listOf("BRSpqZOPEas", "EnFWooVE1C4", "bjouACOL9Ac"))
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intent_controls_activity)

        play_video_button.setOnClickListener(this)
        start_playlist_button.setOnClickListener(this)
        start_video_list_button.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        val chooseIndex = parseInt(choose_index_edit.text.toString(), 0)
        val startTimeMillis = parseInt(choose_start_time_edit.text.toString(), 0) * 1000
        val autoplay = autoplay_checkbox.isChecked
        val boxMode = box_checkbox.isChecked
        var intent: Intent? = null

        when {
            v === play_video_button -> {
                intent = YouTubeStandalonePlayer.createVideoIntent(
                    this,
                    YoutubeConnector.API_KEY,
                    VIDEO_ID,
                    startTimeMillis,
                    autoplay,
                    boxMode
                )
            }
            v === start_playlist_button -> {
                intent = YouTubeStandalonePlayer.createPlaylistIntent(
                    this,
                    YoutubeConnector.API_KEY,
                    PLAYLIST_ID,
                    chooseIndex,
                    startTimeMillis,
                    autoplay,
                    boxMode
                )
            }
            v === start_video_list_button -> {
                intent = YouTubeStandalonePlayer.createVideosIntent(
                    this,
                    YoutubeConnector.API_KEY,
                    VIDEO_LIST_ID,
                    chooseIndex,
                    startTimeMillis,
                    autoplay,
                    boxMode
                )
            }
        }
        if (intent != null) {
            if (canResolveIntent(intent)) {
                startActivityForResult(intent,
                    REQ_START_STANDALONE_PLAYER
                )
            } else {
                // Could not resolve the intent - must need to install or update the YouTube API service.
                YouTubeInitializationResult.SERVICE_MISSING.getErrorDialog(this,
                    REQ_RESOLVE_SERVICE_MISSING
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
            val result = YouTubeStandalonePlayer.getReturnedInitializationResult(data)
            if (result.isUserRecoverableError) {
                result.getErrorDialog(this, 0).show()
            } else {
                val errorMessage = String.format(getString(R.string.error_player), result.toString())
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val resolveInfo = packageManager.queryIntentActivities(intent, 0)
        return resolveInfo.isNotEmpty()
    }

    private fun parseInt(text: String, defaultValue: Int): Int {
        if (!TextUtils.isEmpty(text)) {
            try {
                return text.toInt()
            } catch (e: NumberFormatException) {}
        }
        return defaultValue
    }
}