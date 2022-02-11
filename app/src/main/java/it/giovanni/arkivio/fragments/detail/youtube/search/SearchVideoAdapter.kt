package it.giovanni.arkivio.fragments.detail.youtube.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.giovanni.arkivio.databinding.VideoItemBinding

class SearchVideoAdapter(
    private val context: Context?,
    private var list: List<Video>?,
    private val onItemViewClicked: OnItemViewClicked
) : RecyclerView.Adapter<SearchVideoAdapter.SearchVideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVideoViewHolder {
        val videoItemBinding: VideoItemBinding = VideoItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return SearchVideoViewHolder(videoItemBinding)
    }

    override fun onBindViewHolder(holder: SearchVideoViewHolder, position: Int) {

        val video = list!![position]
        holder.title.text = video.title
        holder.description.text = video.description

        val videoID = "Video ID: " + video.id
        holder.id.text = videoID

        Glide.with(context!!)
            .load(video.url)
            .override(480, 270)
            .centerCrop()
            .into(holder.thumbnail)

        holder.goToPlayerView.setOnClickListener {
            onItemViewClicked.onItemToPlayerViewClicked(video)
        }
        holder.goToPlayerFragment.setOnClickListener {
            onItemViewClicked.onItemToPlayerFragmentClicked(video)
        }
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list?.size!!
    }

    inner class SearchVideoViewHolder(videoItemBinding: VideoItemBinding) : RecyclerView.ViewHolder(videoItemBinding.root) {

        var thumbnail: ImageView = videoItemBinding.videoThumbnail
        var title: TextView = videoItemBinding.videoTitle
        var description: TextView = videoItemBinding.videoDescription
        var id: TextView = videoItemBinding.videoId
        var goToPlayerView: LinearLayout = videoItemBinding.goToPlayerView
        var goToPlayerFragment: LinearLayout = videoItemBinding.goToPlayerFragment
    }

    interface OnItemViewClicked {
        fun onItemToPlayerViewClicked(video: Video?)
        fun onItemToPlayerFragmentClicked(video: Video?)
    }
}