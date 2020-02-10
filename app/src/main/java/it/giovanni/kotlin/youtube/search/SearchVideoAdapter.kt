package it.giovanni.kotlin.youtube.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.giovanni.kotlin.R

class SearchVideoAdapter(
    private val context: Context?,
    private var list: List<Video>?,
    private val onItemViewClicked: OnItemViewClicked
) : RecyclerView.Adapter<SearchVideoAdapter.SearchVideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVideoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
        return SearchVideoViewHolder(itemView)
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
        return if (list == null) 0 else list!!.size
    }

    inner class SearchVideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var thumbnail: ImageView = view.findViewById(R.id.video_thumbnail)
        var title: TextView = view.findViewById(R.id.video_title)
        var description: TextView = view.findViewById(R.id.video_description)
        var id: TextView = view.findViewById(R.id.video_id)
        var goToPlayerView: LinearLayout = view.findViewById(R.id.go_to_player_view)
        var goToPlayerFragment: LinearLayout = view.findViewById(R.id.go_to_player_fragment)
    }

    interface OnItemViewClicked {
        fun onItemToPlayerViewClicked(video: Video?)
        fun onItemToPlayerFragmentClicked(video: Video?)
    }
}