package it.giovanni.arkivio.fragments.detail.dragdrop

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.databinding.FavoriteAvailableItemBinding
import it.giovanni.arkivio.databinding.FavoriteItemBinding

class FavoritesAdapter(
    private val onAdapterListener: OnAdapterListener
) : DragDropAdapter<OldFavorite, RecyclerView.ViewHolder>(diffUtil) {

    inner class FavoriteViewHolder(private val binding: FavoriteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: OldFavorite) {
            binding.favoriteTitle.text = favorite.title
            binding.root.setOnLongClickListener { view ->
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                view?.startDragAndDrop(data, shadowBuilder, view, 0)
                false
            }
            binding.root.setOnDragListener(dragListener)
        }
    }

    inner class AvailableViewHolder(private val binding: FavoriteAvailableItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: OldFavorite) {
            binding.availableTitle.text = favorite.title
            binding.root.setOnLongClickListener { view ->
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                view?.startDragAndDrop(data, shadowBuilder, view, 0)
                false
            }
            binding.root.setOnDragListener(dragListener)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FAVORITE -> {
                FavoriteViewHolder(
                    FavoriteItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                AvailableViewHolder(
                    FavoriteAvailableItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_FAVORITE -> {
                (holder as FavoriteViewHolder).bind(getItem(position))
            }
            VIEW_TYPE_AVAILABLE -> {
                (holder as AvailableViewHolder).bind(getItem(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].isFavorite) VIEW_TYPE_FAVORITE
        else VIEW_TYPE_AVAILABLE
    }

    interface OnAdapterListener {
        fun onAdd(favorite: OldFavorite)
        fun onRemove(favorite: OldFavorite)
        fun onSet(targetIndex: Int, sourceIndex: Int, favorite: OldFavorite)
        fun onSwap(isFavorite: Boolean, from: Int, to: Int)
    }

    companion object {

        private const val VIEW_TYPE_FAVORITE = 0
        private const val VIEW_TYPE_AVAILABLE = 1

        val diffUtil = object : DiffUtil.ItemCallback<OldFavorite>() {
            override fun areItemsTheSame(oldItem: OldFavorite, newItem: OldFavorite): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: OldFavorite, newItem: OldFavorite): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onAdd(item: OldFavorite) {
        onAdapterListener.onAdd(item)
    }

    override fun onRemove(item: OldFavorite) {
        onAdapterListener.onRemove(item)
    }


    override fun onSwap(from: Int, to: Int) {
        if (currentList.any { it.isFavorite }) {
            onAdapterListener.onSwap(true, from, to)
        } else {
            onAdapterListener.onSwap(false, from, to)
        }
    }

    override fun onSet(
        targetIndex: Int,
        sourceIndex: Int,
        targetItem: OldFavorite,
    ) {
        onAdapterListener.onSet(targetIndex, sourceIndex, targetItem)
    }
}