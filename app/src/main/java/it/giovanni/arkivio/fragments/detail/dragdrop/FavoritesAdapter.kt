package it.giovanni.arkivio.fragments.detail.dragdrop

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.databinding.FavoriteAvailableItemBinding
import it.giovanni.arkivio.databinding.FavoriteEmptyItemBinding
import it.giovanni.arkivio.databinding.FavoritePersonalItemBinding

class FavoritesAdapter(
    override val isSwappable: Boolean,
    private val onAdapterListener: OnAdapterListener
) : DragDropAdapter<Favorite, RecyclerView.ViewHolder>(diffUtil) {

    inner class PersonalViewHolder(private val binding: FavoritePersonalItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: Favorite) {
            binding.title.text = favorite.title
            binding.root.setOnLongClickListener { view ->
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                view?.startDragAndDrop(data, shadowBuilder, view, 0)
                false
            }
            binding.item.setOnDragListener(dragListener)
        }
    }

    inner class AvailableViewHolder(private val binding: FavoriteAvailableItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: Favorite) {
            binding.title.text = favorite.title
            binding.root.setOnLongClickListener { view ->
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                view?.startDragAndDrop(data, shadowBuilder, view, 0)
                false
            }
            binding.item.setOnDragListener(dragListener)
        }
    }

    inner class EmptyViewHolder(private val binding: FavoriteEmptyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.item.setOnDragListener(dragListener)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            EMPTY_TYPE -> {
                EmptyViewHolder(
                    FavoriteEmptyItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            PERSONAL_TYPE -> {
                PersonalViewHolder(
                    FavoritePersonalItemBinding.inflate(
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
            EMPTY_TYPE -> {
                (holder as EmptyViewHolder).bind()
            }
            PERSONAL_TYPE -> {
                (holder as PersonalViewHolder).bind(getItem(position))
            }
            AVAILABLE_TYPE -> {
                (holder as AvailableViewHolder).bind(getItem(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position] == null) EMPTY_TYPE
        else if (currentList[position].isPersonal) PERSONAL_TYPE
        else AVAILABLE_TYPE
    }

    interface OnAdapterListener {
        fun onAdd(favorite: Favorite)
        fun onRemove(favorite: Favorite)
        fun onSet(targetIndex: Int, sourceIndex: Int, favorite: Favorite)
        fun onSwap(isPersonal: Boolean, from: Int, to: Int)
    }

    companion object {

        private const val EMPTY_TYPE = 0
        private const val PERSONAL_TYPE = 1
        private const val AVAILABLE_TYPE = 2

        val diffUtil = object : DiffUtil.ItemCallback<Favorite>() {
            override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onAdd(item: Favorite) {
        onAdapterListener.onAdd(item)
    }

    override fun onRemove(item: Favorite) {
        onAdapterListener.onRemove(item)
    }


    override fun onSwap(from: Int, to: Int) {
        if (currentList.any { it.isPersonal }) {
            onAdapterListener.onSwap(true, from, to)
        } else {
            onAdapterListener.onSwap(false, from, to)
        }
    }

    override fun onSet(
        targetIndex: Int,
        sourceIndex: Int,
        targetItem: Favorite,
    ) {
        onAdapterListener.onSet(targetIndex, sourceIndex, targetItem)
    }
}