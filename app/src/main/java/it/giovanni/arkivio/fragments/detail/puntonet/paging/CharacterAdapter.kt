package it.giovanni.arkivio.fragments.detail.puntonet.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import it.giovanni.arkivio.fragments.detail.puntonet.paging.CharacterAdapter.ImageViewHolder
import it.giovanni.arkivio.databinding.PagingItemBinding

class CharacterAdapter : PagingDataAdapter<RickMorty, ImageViewHolder>(diffCallback) {

    inner class ImageViewHolder(val binding: PagingItemBinding) : ViewHolder(binding.root)

    companion object {

        val diffCallback = object : DiffUtil.ItemCallback<RickMorty>() {
            override fun areItemsTheSame(oldItem: RickMorty, newItem: RickMorty): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RickMorty, newItem: RickMorty): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            PagingItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currChar = getItem(position)

        holder.binding.apply {

            holder.itemView.apply {
                characterName.text = "${currChar?.name}"

                val imageUrl: String? = currChar?.image

                Glide.with(context)
                    .load(imageUrl)
                    .into(holder.binding.characterImageView)
            }
        }
    }
}