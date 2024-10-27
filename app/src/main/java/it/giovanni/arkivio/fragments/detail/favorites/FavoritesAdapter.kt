package it.giovanni.arkivio.fragments.detail.favorites

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.databinding.FavoriteAvailableItemBinding
import it.giovanni.arkivio.databinding.FavoriteHeaderItemBinding
import it.giovanni.arkivio.databinding.FavoritePersonalItemBinding
import it.giovanni.arkivio.model.favorite.Favorite
import it.giovanni.arkivio.utils.FavoriteUtils

class FavoritesAdapter(
    private val isPersonal: Boolean,
    private val onAdapterListener: OnAdapterListener
) : DragListAdapter<Favorite, RecyclerView.ViewHolder>(diffUtil) {

    private var isEditMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PERSONAL -> {
                val binding = FavoritePersonalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PersonalViewHolder(binding)
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
            is PersonalViewHolder -> {
                if (isPersonal)
                    holder.bind(favorite)
            }
            is AvailableViewHolder -> {
                if (!isPersonal)
                    holder.bind(favorite)
            }
            is HeaderViewHolder -> {
                if (!isPersonal)
                    holder.bind(favorite.availableTitle)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val favorite = getItem(position)
        if (isPersonal) {
            return VIEW_TYPE_PERSONAL
        } else {
            if (position == 0 || favorite.availableTitle != getItem(position - 1).availableTitle) {
                return VIEW_TYPE_HEADER
            }
            return VIEW_TYPE_AVAILABLE
        }
    }

    override fun onSet(targetIndex: Int, sourceIndex: Int, targetItem: Favorite) {
        onAdapterListener.onSet(isPersonal = isPersonal, targetIndex, sourceIndex, targetItem)
    }

    override fun onAdd(item: Favorite) {
        onAdapterListener.onAdd(isPersonal = isPersonal, item)
    }

    override fun onRemove(item: Favorite) {
        onAdapterListener.onRemove(isPersonal = isPersonal, item)
    }

    override fun onSwap(from: Int, to: Int) {
        onAdapterListener.onSwap(isPersonal = isPersonal, from, to)
    }

    inner class PersonalViewHolder(private val binding: FavoritePersonalItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(personal: Favorite) {
            binding.personalTitle.text = personal.title
            FavoriteUtils.setImageByContentPath(binding.personalPoster, personal.images?.get(0)?.contentPath)

            if (personal.identifier == EDIT_IDENTIFIER) {
                binding.root.setOnClickListener {
                    // Remove the edit item and enable edit mode.
                    val newPersonals = currentList.toMutableList().apply {
                        removeAt(bindingAdapterPosition)
                    }
                    notifyDataSetChanged()
                    submitList(newPersonals)
                    isEditMode = true
                }
            } else {
                if (isEditMode) {
                    binding.personalBorder.visibility = View.VISIBLE
                    binding.root.setOnLongClickListener { view ->
                        val data = ClipData.newPlainText("", "")
                        val shadowBuilder = DragShadowBuilder(view)
                        view.startDragAndDrop(data, shadowBuilder, view, 0)
                        false
                    }
                    binding.root.setOnDragListener(dragListener)
                } else {
                    binding.personalBorder.visibility = View.GONE
                }
            }
        }
    }

    inner class AvailableViewHolder(private val binding: FavoriteAvailableItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(available: Favorite) {
            binding.availableTitle.text = available.title
            FavoriteUtils.setImageByContentPath(binding.availablePoster, available.images?.get(0)?.contentPath)

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
        const val VIEW_TYPE_PERSONAL = 0
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