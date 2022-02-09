package it.giovanni.arkivio.fragments.detail.youtube

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.youtube.player.YouTubeIntents
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.YoutubeLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.fragments.detail.youtube.intents.YouTubeIntentsActivity
import it.giovanni.arkivio.fragments.detail.youtube.videolist.VideoListActivity
import it.giovanni.arkivio.fragments.detail.youtube.videowall.VideoWallActivity
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class YouTubeFragment : DetailFragment() {

    private var layoutBinding: YoutubeLayoutBinding? = null
    private val binding get() = layoutBinding

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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = YoutubeLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val youTubeVersion = YouTubeIntents.getInstalledYouTubeVersionName(context)
        if (youTubeVersion != null) {
            val text = String.format(getString(R.string.youtube_version), youTubeVersion)
            binding?.labelYoutubeVersion?.text = text
        } else binding?.labelYoutubeVersion?.text = getString(R.string.youtube_not_installed)

        binding?.labelSearch?.setOnClickListener {
            currentActivity.openDetail(Globals.SEARCH_VIDEO, null)
        }

        binding?.labelVideoWall?.setOnClickListener {
            startActivity(Intent(context, VideoWallActivity::class.java))
        }

        binding?.labelVideoList?.setOnClickListener {
            startActivity(Intent(context, VideoListActivity::class.java))
        }

        binding?.labelFullScreen?.setOnClickListener {
            startActivity(Intent(context, FullScreenActivity::class.java))
        }

        binding?.labelActionBar?.setOnClickListener {
            startActivity(Intent(context, ActionBarActivity::class.java))
        }

        binding?.labelPlayerControls?.setOnClickListener {
            startActivity(Intent(context, PlayerControlsActivity::class.java))
        }

        binding?.labelIntentControls?.setOnClickListener {
            startActivity(Intent(context, IntentControlsActivity::class.java))
        }

        binding?.labelYoutubeIntents?.setOnClickListener {
            startActivity(Intent(context, YouTubeIntentsActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}