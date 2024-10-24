package it.giovanni.arkivio.fragments.detail.drag

import android.content.ClipData
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.databinding.FavoriteAvailableItemBinding
import it.giovanni.arkivio.databinding.FavoriteHeaderItemBinding
import it.giovanni.arkivio.databinding.FavoritePersonalItemBinding
import it.giovanni.arkivio.model.favorite.FavoriteUtils
import it.giovanni.arkivio.model.favorite.Personal

class DragAdapter(
    private val isPersonalList: Boolean,
    var favorites: MutableList<Personal>,
    private val listener: Listener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        val favorite = favorites[position]
        when (holder) {
            is PersonalViewHolder -> {
                if (isPersonalList)
                holder.bind(favorite)
            }
            is AvailableViewHolder -> {
                if (!isPersonalList)
                    holder.bind(favorite)
            }
            is HeaderViewHolder -> {
                if (!isPersonalList)
                    holder.bind(favorite.availableTitle)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (isPersonalList) {
            return VIEW_TYPE_PERSONAL
        } else {
            if (position == 0 || favorites[position].availableTitle != favorites[position - 1].availableTitle) {
                return VIEW_TYPE_HEADER
            }
            return VIEW_TYPE_AVAILABLE
        }
    }

    override fun getItemCount(): Int {
        return favorites.size
    }

    fun updateFavorites(list: MutableList<Personal>) {
        this.favorites = list
    }

    val dragInstance: DragListener?
        get() = if (listener != null) {
            DragListener(listener)
        } else {
            null
        }

    inner class PersonalViewHolder(private val binding: FavoritePersonalItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(personal: Personal) {
            binding.title.text = personal.title
            FavoriteUtils.setImageByContentPath(binding.personalImage, personal.images?.get(0)?.contentPath)
            binding.item.tag = adapterPosition

            if (personal.identifier == EDIT_IDENTIFIER) {
                binding.item.setOnClickListener {
                    // Remove the edit item and enable edit mode.
                    val newFavorites = favorites.toMutableList().apply {
                        removeAt(adapterPosition)
                    }
                    updateFavorites(newFavorites)
                    isEditMode = true
                    notifyDataSetChanged()
                }
            } else {
                // For all other items, toggle border based on isEditMode flag.
                if (isEditMode) {
                    binding.itemBorder.visibility = View.VISIBLE

                    // Enable the long click listener.
                    binding.root.setOnLongClickListener { view ->
                        val data = ClipData.newPlainText("", "")
                        val shadowBuilder = DragShadowBuilder(view)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            view.startDragAndDrop(data, shadowBuilder, view, 0)
                        } else {
                            view.startDrag(data, shadowBuilder, view, 0)
                        }
                        false
                    }
                    binding.item.setOnDragListener(DragListener(listener))
                } else {
                    // Consider to disable the long click listener.
                    binding.itemBorder.visibility = View.GONE
                }
            }
        }
    }

    inner class AvailableViewHolder(private val binding: FavoriteAvailableItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(personal: Personal) {
            binding.title.text = personal.title
            FavoriteUtils.setImageByContentPath(binding.availableImage, personal.images?.get(0)?.contentPath)
            binding.item.tag = adapterPosition

            binding.root.setOnLongClickListener { view ->
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = DragShadowBuilder(view)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, view, 0)
                } else {
                    view.startDrag(data, shadowBuilder, view, 0)
                }
                false
            }
            binding.item.setOnDragListener(DragListener(listener))
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
    }
}