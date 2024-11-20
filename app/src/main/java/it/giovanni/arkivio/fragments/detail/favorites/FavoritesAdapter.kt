package it.giovanni.arkivio.fragments.detail.favorites

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import android.widget.ImageView
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
    private val onAdapterListener: OnAdapterListener,
) : DragListAdapter2<Favorite, RecyclerView.ViewHolder>(diffUtil) {

    private var isEditMode = false
    private var showBadge = false
    private var isMaxReached = false

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
                    holder.bind(favorite)
                }
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

    inner class PersonalViewHolder(private val binding: FavoritePersonalItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private var valueAnimator: ValueAnimator? = null

        fun bind(personal: Favorite) {
            binding.personalTitle.text = personal.title
            FavoriteUtils.setImageByContentPath(binding.personalPoster, personal.images?.get(0)?.contentPath)

            if (personal.identifier == EDIT_IDENTIFIER) {
                binding.personalBorder.visibility = View.GONE
                binding.badgeRemove.visibility = View.GONE
                binding.root.setOnClickListener {
                    if (!isEditMode) {
                        onAdapterListener.onEditModeRemoved(bindingAdapterPosition)
                        onAdapterListener.onEditModeChanged(true)
                    }
                }
            } else {
                if (isEditMode) {
                    binding.personalBorder.visibility = View.VISIBLE
                    binding.badgeRemove.visibility = View.VISIBLE
                    startPersonalShimmerEffect(valueAnimator, binding)

                    if (!isMaxReached) {
                        binding.root.setOnDragListener(dragListener)
                        binding.root.setOnLongClickListener { view ->
                            val data = ClipData.newPlainText("", "")
                            val shadowBuilder = FavoriteDragShadowBuilder(view, ContextCompat.getDrawable(view.context, R.drawable.circle_item_empty), showBadge) // DragShadowBuilder(view)
                            view.startDragAndDrop(data, shadowBuilder, view, 0)
                            false
                        }
                    } else {
                        binding.root.setOnDragListener(null)
                        binding.root.setOnLongClickListener(null)
                    }

                    binding.root.setOnClickListener {
                        onAdapterListener.onDrop(isPersonal = true, sourcePosition = bindingAdapterPosition, targetPosition = 0)
                    }
                } else {
                    binding.personalBorder.visibility = View.GONE
                    binding.badgeRemove.visibility = View.GONE
                    stopPersonalShimmerEffect(valueAnimator, binding)

                    binding.root.setOnDragListener(null)
                    binding.root.setOnLongClickListener(null)

                    binding.root.setOnClickListener {
                        // TODO: Open detail
                    }
                }
            }
        }
    }

    inner class AvailableViewHolder(private val binding: FavoriteAvailableItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private var valueAnimator: ValueAnimator? = null

        fun bind(available: Favorite) {
            binding.availableTitle.text = available.title
            FavoriteUtils.setImageByContentPath(binding.availablePoster, available.images?.get(0)?.contentPath)
            if (isEditMode) {
                binding.availableBorder.visibility = View.VISIBLE
                binding.badgeAdd.visibility = View.VISIBLE
                startAvailableShimmerEffect(valueAnimator, binding)

                binding.root.setOnDragListener(dragListener)
                binding.root.setOnLongClickListener { view ->
                    val data = ClipData.newPlainText("", "")
                    val shadowBuilder = DragShadowBuilder(view)
                    view.startDragAndDrop(data, shadowBuilder, view, 0)
                    false
                }

                binding.root.setOnClickListener {
                    onAdapterListener.onDrop(isPersonal = false, sourcePosition = bindingAdapterPosition, targetPosition = 0)
                }
            } else {
                binding.availableBorder.visibility = View.GONE
                binding.badgeAdd.visibility = View.GONE
                stopAvailableShimmerEffect(valueAnimator, binding)

                binding.root.setOnDragListener(null)
                binding.root.setOnLongClickListener(null)

                binding.root.setOnClickListener {
                    // TODO: Open detail
                }
            }
        }
    }

    inner class HeaderViewHolder(val binding: FavoriteHeaderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerTitle: String?) {
            binding.headerTitle.text = headerTitle
        }
    }

    private fun startPersonalShimmerEffect(valueAnimator: ValueAnimator?, binding: FavoritePersonalItemBinding) {
        var animator = valueAnimator
        if (animator == null || !animator.isRunning) {
            val startColor = ContextCompat.getColor(binding.root.context, R.color.white)
            val borderEndColor = ContextCompat.getColor(binding.root.context, R.color.white_alpha30)
            val badgeEndColor = ContextCompat.getColor(binding.root.context, R.color.red_500)

            animator = ValueAnimator.ofArgb(startColor, borderEndColor).apply {
                duration = 2000L
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE

                addUpdateListener { animator ->
                    val animatedValue: Int = animator.animatedValue as Int
                    val background: Drawable = binding.personalBorder.background
                    if (background is GradientDrawable) {
                        background.setStroke(2.dpToPx(binding.root.context), animatedValue)
                    }
                }
                start()
            }

            animator = ValueAnimator.ofArgb(startColor, badgeEndColor).apply {
                duration = 2000L
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE

                addUpdateListener { animator ->
                    val animatedValue: Int = animator.animatedValue as Int
                    val badge: ImageView = binding.badgeRemove
                    val badgeBackground = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        color = ColorStateList.valueOf(animatedValue)
                    }
                    badge.background = badgeBackground
                }
                start()
            }
        }
    }

    private fun startAvailableShimmerEffect(valueAnimator: ValueAnimator?, binding: FavoriteAvailableItemBinding) {
        var animator = valueAnimator
        if (animator == null || !animator.isRunning) {
            val startColor = ContextCompat.getColor(binding.root.context, R.color.white)
            val borderEndColor = ContextCompat.getColor(binding.root.context, R.color.white_alpha30)
            val badgeEndColor = ContextCompat.getColor(binding.root.context, R.color.green_500)

            animator = ValueAnimator.ofArgb(startColor, borderEndColor).apply {
                duration = 2000L
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE

                addUpdateListener { animator ->
                    val animatedValue: Int = animator.animatedValue as Int
                    val background: Drawable = binding.availableBorder.background
                    if (background is GradientDrawable) {
                        background.setStroke(2.dpToPx(binding.root.context), animatedValue)
                    }
                }
                start()
            }

            animator = ValueAnimator.ofArgb(startColor, badgeEndColor).apply {
                duration = 2000L
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE

                addUpdateListener { animator ->
                    val animatedValue: Int = animator.animatedValue as Int
                    val badge: ImageView = binding.badgeAdd
                    val badgeBackground = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        color = ColorStateList.valueOf(animatedValue)
                    }
                    badge.background = badgeBackground
                }
                start()
            }
        }
    }

    private fun stopPersonalShimmerEffect(valueAnimator: ValueAnimator?, binding: FavoritePersonalItemBinding) {
        valueAnimator?.cancel()

        val background: Drawable = binding.personalBorder.background
        if (background is GradientDrawable) {
            background.setStroke(2.dpToPx(binding.root.context), ContextCompat.getColor(binding.root.context, R.color.white))
        }

        val badge: ImageView = binding.badgeRemove
        val badgeBackground = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            color = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.white))
        }
        badge.background = badgeBackground
    }

    private fun stopAvailableShimmerEffect(valueAnimator: ValueAnimator?, binding: FavoriteAvailableItemBinding) {
        valueAnimator?.cancel()

        val background: Drawable = binding.availableBorder.background
        if (background is GradientDrawable) {
            background.setStroke(2.dpToPx(binding.root.context), ContextCompat.getColor(binding.root.context, R.color.white))
        }

        val badge: ImageView = binding.badgeAdd
        val badgeBackground = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            color = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.white))
        }
        badge.background = badgeBackground
    }

    private fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics).toInt()
    }

    override fun onDragLocation(showBadge: Boolean) {
        this.showBadge = showBadge
    }

    override fun onSwap(sourcePosition: Int, targetPosition: Int) {
        onAdapterListener.onSwap(isPersonal = isPersonal, sourcePosition, targetPosition)
    }

    override fun onDrop(sourcePosition: Int, targetPosition: Int) {
        onAdapterListener.onDrop(isPersonal = isPersonal, sourcePosition, targetPosition)
    }

    override fun onSwapEntered(sourcePosition: Int, targetPosition: Int) {
        onAdapterListener.onSwapEntered(isPersonal = isPersonal, sourcePosition, targetPosition)
    }

    override fun onSwapEnded(sourcePosition: Int, targetPosition: Int) {
        onAdapterListener.onSwapEnded(isPersonal = isPersonal, sourcePosition, targetPosition)
    }

    override fun onDragEntered(sourcePosition: Int, targetPosition: Int) {
        onAdapterListener.onDragEntered(isPersonal = isPersonal, sourcePosition, targetPosition)
    }

    override fun onDragEnded(sourcePosition: Int, targetPosition: Int) {
        onAdapterListener.onDragEnded(isPersonal = isPersonal, sourcePosition, targetPosition)
    }

    fun setEditMode(isEditMode: Boolean) {
        this.isEditMode = isEditMode
        notifyDataSetChanged()
    }

    fun enableDragDrop(isMaxReached: Boolean) {
        this.isMaxReached = isMaxReached
    }

    companion object {
        const val VIEW_TYPE_PERSONAL = 0
        const val VIEW_TYPE_AVAILABLE = 1
        const val VIEW_TYPE_HEADER = 2
        const val EDIT_IDENTIFIER = "edit"
        const val MAX_FAVORITES_SIZE = 7

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