package it.giovanni.arkivio.fragments.detail.favorites

import android.content.ClipData
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.databinding.FavoriteAvailableItemBinding
import it.giovanni.arkivio.databinding.FavoriteHeaderItemBinding
import it.giovanni.arkivio.databinding.FavoriteItemBinding
import it.giovanni.arkivio.model.favorite.Favorite
import it.giovanni.arkivio.model.favorite.FavoriteUtils

class FavoritesAdapter(
    private val isFavoriteList: Boolean,
    private val onAdapterListener: OnAdapterListener
) : DragListAdapter<Favorite, RecyclerView.ViewHolder>(diffUtil) {

    private var isEditMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FAVORITE -> {
                val binding = FavoriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FavoriteViewHolder(binding)
            }
            VIEW_TYPE_HEADER -> {
                val binding = FavoriteHeaderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_AVAILABLE -> {
                val binding = FavoriteAvailableItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AvailableViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val favorite = getItem(position)
        when (holder) {
            is FavoriteViewHolder -> {
                if (isFavoriteList)
                    holder.bind(favorite)
            }
            is AvailableViewHolder -> {
                if (!isFavoriteList)
                    holder.bind(favorite)
            }
            is HeaderViewHolder -> {
                if (!isFavoriteList)
                    holder.bind(favorite.availableTitle)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val favorite = getItem(position)
        if (isFavoriteList) {
            return VIEW_TYPE_FAVORITE
        } else {
            if (position == 0 || favorite.availableTitle != getItem(position - 1).availableTitle) {
                return VIEW_TYPE_HEADER
            }
            return VIEW_TYPE_AVAILABLE
        }
    }

    override fun onSet(targetIndex: Int, sourceIndex: Int, targetItem: Favorite) {
        onAdapterListener.onSet(targetIndex, sourceIndex, targetItem)
    }

    override fun onAdd(item: Favorite) {
        onAdapterListener.onAdd(item)
    }

    override fun onRemove(item: Favorite) {
        onAdapterListener.onRemove(item)
    }

    override fun onSwap(from: Int, to: Int) {
        if (currentList.any { it.availableTitle == null }) {
            onAdapterListener.onSwap(true, from, to)
        } else {
            onAdapterListener.onSwap(false, from, to)
        }
    }

    inner class FavoriteViewHolder(private val binding: FavoriteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: Favorite) {
            binding.favoriteTitle.text = favorite.title
            FavoriteUtils.setImageByContentPath(binding.favoritePoster, favorite.images?.get(0)?.contentPath)
            Log.i("[DRAG]", "DragAdapter - bindingAdapterPosition: $bindingAdapterPosition")

            if (favorite.identifier == EDIT_IDENTIFIER) {
                binding.root.setOnClickListener {
                    // Remove the edit item and enable edit mode.
                    val newFavorites = currentList.toMutableList().apply {
                        removeAt(bindingAdapterPosition)
                    }
                    notifyDataSetChanged()
                    submitList(newFavorites)
                    isEditMode = true
                }
            } else {
                if (isEditMode) {
                    binding.favoriteBorder.visibility = View.VISIBLE
                    binding.root.setOnLongClickListener { view ->
                        val data = ClipData.newPlainText("", "")
                        val shadowBuilder = DragShadowBuilder(view)
                        view.startDragAndDrop(data, shadowBuilder, view, 0)
                        false
                    }
                    binding.root.setOnDragListener(dragListener)
                } else {
                    binding.favoriteBorder.visibility = View.GONE
                }
            }
        }
    }

    inner class AvailableViewHolder(private val binding: FavoriteAvailableItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: Favorite) {
            binding.availableTitle.text = favorite.title
            FavoriteUtils.setImageByContentPath(binding.availablePoster, favorite.images?.get(0)?.contentPath)

            binding.root.setOnLongClickListener { view ->
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = DragShadowBuilder(view)
                view.startDragAndDrop(data, shadowBuilder, view, 0)
                false
            }
            binding.root.setOnDragListener(dragListener)
        }
    }

    inner class HeaderViewHolder(val binding: FavoriteHeaderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerTitle: String?) {
            binding.headerTitle.text = headerTitle
        }
    }

    companion object {
        const val VIEW_TYPE_FAVORITE = 0
        const val VIEW_TYPE_AVAILABLE = 1
        const val VIEW_TYPE_HEADER = 2
        const val EDIT_IDENTIFIER = "edit"

        val diffUtil = object : DiffUtil.ItemCallback<Favorite>() {
            override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
                return oldItem.identifier == newItem.identifier
            }

            override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
                return oldItem == newItem
            }
        }
    }
}