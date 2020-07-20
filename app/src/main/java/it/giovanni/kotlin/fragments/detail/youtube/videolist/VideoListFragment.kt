package it.giovanni.kotlin.fragments.detail.youtube.videolist

import android.app.ListFragment
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ListView
import it.giovanni.kotlin.R
import java.util.*

/**
 * A fragment that shows a static list of videos.
 */

@Suppress("DEPRECATION")
class VideoListFragment : ListFragment() {

    companion object {
        private var VIDEO_LIST: List<VideoListActivity.VideoEntry>? = null

        init {
            val list: MutableList<VideoListActivity.VideoEntry> = ArrayList()
            list.add(VideoListActivity.VideoEntry("La mia storia", "BRSpqZOPEas"))
            list.add(VideoListActivity.VideoEntry("LUNA? ci siamo stati eccome!", "EnFWooVE1C4"))
            list.add(VideoListActivity.VideoEntry("COMPLOTTI: perché ci crediamo?", "bjouACOL9Ac"))
            list.add(VideoListActivity.VideoEntry("ILLUSIONI DEL CERVELLO: come la mente ci inganna", "ANG1YDefd40"))
            list.add(VideoListActivity.VideoEntry("UFO nell'arte? I dipinti svelati", "Zbigp4znyjQ"))
            list.add(VideoListActivity.VideoEntry("Qual è il segreto del mago Houdini?", "kWV3Man-Alw"))
            list.add(VideoListActivity.VideoEntry("Visitatori dal FUTURO? 5 casi straordinari!", "u-_LrqNR6fI"))
            list.add(VideoListActivity.VideoEntry("PIRAMIDI egizie: i MISTERI svelati!", "zEuRWLoJ84c"))
            VIDEO_LIST = Collections.unmodifiableList(list)
        }
    }

    private var adapter: VideoListAdapter? = null
    private var videoBox: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = VideoListAdapter(activity, VIDEO_LIST)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        videoBox = activity.findViewById(R.id.video_box)
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listAdapter = adapter
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {

        val videoId = VIDEO_LIST!![position].videoId
        val videoFragment = fragmentManager.findFragmentById(R.id.video_fragment_container) as VideoFragment
        videoFragment.setVideoId(videoId)
        // The videoBox is INVISIBLE if no video was previously selected, so we need to show it now.
        if (videoBox!!.visibility != View.VISIBLE) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                videoBox!!.translationY = videoBox!!.height.toFloat()
            }
            videoBox!!.visibility = View.VISIBLE
        }
        // If the fragment is off the screen, we animate it in.
        if (videoBox!!.translationY > 0) {
            videoBox!!.animate().translationY(0f).duration = VideoListActivity.ANIMATION_DURATION_MILLIS.toLong()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter!!.releaseLoaders()
    }
}