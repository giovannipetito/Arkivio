package it.giovanni.arkivio.fragments.detail.favorites

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.databinding.FavoriteAvailableItemBinding
import it.giovanni.arkivio.databinding.FavoriteHeaderItemBinding
import it.giovanni.arkivio.databinding.FavoritePersonalItemBinding
import it.giovanni.arkivio.model.favorite.Favorite
import it.giovanni.arkivio.utils.FavoriteUtils
import it.giovanni.arkivio.R

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
                if (isPersonal) {
                    holder.bind(favorite, isEditMode)
                    holder.itemView.setOnClickListener {
                        onAdapterListener.onEditModeChanged(true)
                    }
                }
            }
            is AvailableViewHolder -> {
                if (!isPersonal)
                    holder.bind(favorite, isEditMode)
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

    inner class PersonalViewHolder(private val binding: FavoritePersonalItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private var borderValueAnimator: ValueAnimator? = null

        fun bind(personal: Favorite, isEditMode: Boolean) {
            binding.personalTitle.text = personal.title
            FavoriteUtils.setImageByContentPath(binding.personalPoster, personal.images?.get(0)?.contentPath)

            if (personal.identifier == EDIT_IDENTIFIER) {
                binding.root.visibility = if (isEditMode) View.GONE else View.VISIBLE

                binding.root.setOnClickListener {
                    if (!isEditMode) {
                        val newPersonals = currentList.toMutableList().apply {
                            removeAt(bindingAdapterPosition)
                        }
                        submitList(newPersonals)
                        notifyItemRangeChanged(0, newPersonals.size)
                    }
                }
            } else {
                binding.root.visibility = View.VISIBLE

                if (isEditMode) {
                    binding.personalBorder.visibility = View.VISIBLE
                    startShimmerEffect(borderValueAnimator, binding)

                    binding.root.setOnLongClickListener { view ->
                        val data = ClipData.newPlainText("", "")
                        val shadowBuilder = DragShadowBuilder(view)
                        view.startDragAndDrop(data, shadowBuilder, view, 0)
                        false
                    }
                    binding.root.setOnDragListener(dragListener)
                } else {
                    binding.personalBorder.visibility = View.GONE
                    stopShimmerEffect(borderValueAnimator, binding)

                    binding.root.setOnLongClickListener(null)
                    binding.root.setOnDragListener(null)
                }
            }
        }
    }

    inner class AvailableViewHolder(private val binding: FavoriteAvailableItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(available: Favorite, isEditMode: Boolean) {
            binding.availableTitle.text = available.title
            FavoriteUtils.setImageByContentPath(binding.availablePoster, available.images?.get(0)?.contentPath)
            if (isEditMode) {
                binding.root.setOnLongClickListener { view ->
                    val data = ClipData.newPlainText("", "")
                    val shadowBuilder = DragShadowBuilder(view)
                    view.startDragAndDrop(data, shadowBuilder, view, 0)
                    false
                }
                binding.root.setOnDragListener(dragListener)
            } else {
                binding.root.setOnLongClickListener(null)
                binding.root.setOnDragListener(null)
            }
        }
    }

    inner class HeaderViewHolder(val binding: FavoriteHeaderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerTitle: String?) {
            binding.headerTitle.text = headerTitle
        }
    }

    private fun startShimmerEffect(borderAnimator: ValueAnimator?, binding: FavoritePersonalItemBinding) {
        var animator = borderAnimator
        if (animator == null || !animator.isRunning) {
            val startColor = ContextCompat.getColor(binding.root.context, R.color.white)
            val endColor = ContextCompat.getColor(binding.root.context, R.color.grey_3)

            animator = ValueAnimator.ofArgb(startColor, endColor).apply {
                duration = 1000L
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE

                addUpdateListener { animator ->
                    val animatedValue = animator.animatedValue as Int
                    val background = binding.personalBorder.background
                    if (background is GradientDrawable) {
                        background.setStroke(2.dpToPx(binding.root.context), animatedValue)
                    }
                }
                start()
            }
        }
    }

    private fun stopShimmerEffect(borderAnimator: ValueAnimator?, binding: FavoritePersonalItemBinding) {
        borderAnimator?.cancel()
        val background = binding.personalBorder.background
        if (background is GradientDrawable) {
            background.setStroke(
                2.dpToPx(binding.root.context),
                ContextCompat.getColor(binding.root.context, R.color.white)
            )
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics).toInt()
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

    fun setEditMode(isEditMode: Boolean) {
        this.isEditMode = isEditMode
        notifyDataSetChanged()
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