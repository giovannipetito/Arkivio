package it.giovanni.kotlin.fragments.detail.youtube

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.youtube.player.YouTubeIntents
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.DetailFragment
import it.giovanni.kotlin.utils.Globals
import it.giovanni.kotlin.fragments.detail.youtube.intents.YouTubeIntentsActivity
import it.giovanni.kotlin.fragments.detail.youtube.videolist.VideoListActivity
import it.giovanni.kotlin.fragments.detail.youtube.videowall.VideoWallActivity
import kotlinx.android.synthetic.main.youtube_layout.*

class YouTubeFragment : DetailFragment() {

    private var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.youtube_layout
    }

    override fun getTitle(): Int {
        return R.string.youtube_title
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val youTubeVersion = YouTubeIntents.getInstalledYouTubeVersionName(context)
        if (youTubeVersion != null) {
            val text = String.format(getString(R.string.youtube_version), youTubeVersion)
            label_youtube_version.text = text
        } else label_youtube_version.text = getString(R.string.youtube_not_installed)

        label_search.setOnClickListener {
            currentActivity.openDetail(Globals.SEARCH_VIDEO, null)
        }

        label_videowall.setOnClickListener {
            startActivity(Intent(context, VideoWallActivity::class.java))
        }

        label_videolist.setOnClickListener {
            startActivity(Intent(context, VideoListActivity::class.java))
        }

        label_fullscreen.setOnClickListener {
            startActivity(Intent(context, FullScreenActivity::class.java))
        }

        label_actionbar.setOnClickListener {
            startActivity(Intent(context, ActionBarActivity::class.java))
        }

        label_player_controls.setOnClickListener {
            startActivity(Intent(context, PlayerControlsActivity::class.java))
        }

        label_intent_controls.setOnClickListener {
            startActivity(Intent(context, IntentControlsActivity::class.java))
        }

        label_youtube_intents.setOnClickListener {
            startActivity(Intent(context, YouTubeIntentsActivity::class.java))
        }
    }
}