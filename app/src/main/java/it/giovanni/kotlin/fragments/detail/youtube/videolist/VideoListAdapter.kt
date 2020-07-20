package it.giovanni.kotlin.fragments.detail.youtube.videolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.detail.youtube.YoutubeConnector
import java.util.ArrayList
import java.util.HashMap

class VideoListAdapter internal constructor(context: Context?, private val entries: List<VideoListActivity.VideoEntry>?) : BaseAdapter() {

    private val entryViews: List<View>
    private val thumbnailViewToLoaderMap: MutableMap<YouTubeThumbnailView, YouTubeThumbnailLoader>
    private val inflater: LayoutInflater
    private val thumbnailListener: ThumbnailListener

    init {
        entryViews = ArrayList()
        thumbnailViewToLoaderMap = HashMap()
        inflater = LayoutInflater.from(context)
        thumbnailListener = ThumbnailListener()
    }

    fun releaseLoaders() {
        for (loader in thumbnailViewToLoaderMap.values) {
            loader.release()
        }
    }

    override fun getCount(): Int {
        return entries?.size!!
    }

    override fun getItem(position: Int): VideoListActivity.VideoEntry {
        return entries!![position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        val entry = entries!![position]
        if (view == null) {
            view = inflater.inflate(R.layout.video_list_item, parent, false)
            val thumbnail: YouTubeThumbnailView = view.findViewById(R.id.thumbnail)
            thumbnail.tag = entry.videoId
            thumbnail.initialize(YoutubeConnector.API_KEY, thumbnailListener)
        } else {
            val thumbnail: YouTubeThumbnailView = view.findViewById(R.id.thumbnail)
            val loader = thumbnailViewToLoaderMap[thumbnail]
            if (loader == null) {
                thumbnail.tag = entry.videoId
            } else {
                thumbnail.setImageResource(R.drawable.loading_thumbnail)
                loader.setVideo(entry.videoId)
            }
        }
        val label = view?.findViewById<TextView>(R.id.thumbnail_text)
        label?.text = entry.text
        return view
    }

    private inner class ThumbnailListener : YouTubeThumbnailView.OnInitializedListener, YouTubeThumbnailLoader.OnThumbnailLoadedListener {

        override fun onInitializationSuccess(view: YouTubeThumbnailView, loader: YouTubeThumbnailLoader) {
            loader.setOnThumbnailLoadedListener(this)
            thumbnailViewToLoaderMap[view] = loader
            view.setImageResource(R.drawable.loading_thumbnail)
            val videoId = view.tag as String
            loader.setVideo(videoId)
        }

        override fun onInitializationFailure(view: YouTubeThumbnailView, loader: YouTubeInitializationResult) {
            view.setImageResource(R.drawable.no_thumbnail)
        }

        override fun onThumbnailLoaded(view: YouTubeThumbnailView, videoId: String) {}

        override fun onThumbnailError(view: YouTubeThumbnailView, errorReason: YouTubeThumbnailLoader.ErrorReason) {
            view.setImageResource(R.drawable.no_thumbnail)
        }
    }
}