package it.giovanni.arkivio.fragments.detail.drag

import android.annotation.SuppressLint
import android.content.ClipData
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.databinding.FavoriteAvailableItemBinding
import it.giovanni.arkivio.databinding.FavoriteHeaderItemBinding
import it.giovanni.arkivio.databinding.FavoritePersonalItemBinding
import it.giovanni.arkivio.model.favorite.Available
import it.giovanni.arkivio.model.favorite.FavoriteUtils
import it.giovanni.arkivio.model.favorite.Child
import it.giovanni.arkivio.model.favorite.Personal

class DragAdapter(
    var personals: MutableList<Personal>,
    var availables: MutableList<Available>,
    private val listener: Listener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), OnTouchListener {

    inner class PersonalViewHolder(private val binding: FavoritePersonalItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(personal: Personal) {
            binding.title.text = personal.title
            FavoriteUtils.setImageByContentPath(binding.personalImage, personal.images[0].contentPath)
            binding.item.tag = adapterPosition
            binding.item.setOnTouchListener(this@DragAdapter)
            binding.item.setOnDragListener(DragListener(listener))
        }
    }

    inner class AvailableViewHolder(private val binding: FavoriteAvailableItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(child: Child) {
            binding.title.text = child.title
            FavoriteUtils.setImageByContentPath(binding.availableImage, child.images[0].contentPath)
            binding.item.tag = adapterPosition
            binding.item.setOnTouchListener(this@DragAdapter)
            binding.item.setOnDragListener(DragListener(listener))
        }
    }

    inner class HeaderViewHolder(val binding: FavoriteHeaderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(available: Available?) {
            binding.headerTitle.text = available?.title
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (personals.isNotEmpty()) {
            return PERSONAL_TYPE
        } else {
            // Determine if the item at this position is a header or a child.
            var count = 0
            availables.forEach { available ->
                if (position == count) {
                    return HEADER_TYPE
                }
                count++
                if (position < count + available.children.size) {
                    return AVAILABLE_TYPE
                }
                count += available.children.size
            }
        }
        return AVAILABLE_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PERSONAL_TYPE -> {
                val binding = FavoritePersonalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PersonalViewHolder(binding)
            }
            HEADER_TYPE -> {
                val binding = FavoriteHeaderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            else -> {
                val binding = FavoriteAvailableItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AvailableViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PersonalViewHolder) {
            holder.bind(personals[position])
        } else {
            var count = 0
            availables.forEach { favorite ->
                if (position == count) {
                    // It's a header
                    val headerHolder = holder as HeaderViewHolder
                    headerHolder.bind(favorite)
                    return
                }
                count++
                if (position < count + favorite.children.size) {
                    // It's a child
                    val childIndex = position - count
                    val child = favorite.children[childIndex]
                    val childHolder = holder as AvailableViewHolder
                    childHolder.bind(child)
                    return
                }
                count += favorite.children.size
            }
        }
    }

    override fun getItemCount(): Int {
        if (personals.isNotEmpty()) {
            return personals.size
        } else {
            var count = availables.size // Start with header count
            availables.forEach { count += it.children.size } // Add child count for each header
            return count
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = DragShadowBuilder(view)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(data, shadowBuilder, view, 0)
            } else {
                view.startDrag(data, shadowBuilder, view, 0)
            }
            return true
        }
        return false
    }

    fun updatePersonalFavorites(list: MutableList<Personal>) {
        this.personals = list
    }

    fun updateAvailableFavorites(list: MutableList<Available>) {
        this.availables = list
    }

    val dragInstance: DragListener?
        get() = if (listener != null) {
            DragListener(listener)
        } else {
            null
        }

    companion object {
        const val PERSONAL_TYPE = 1
        const val AVAILABLE_TYPE = 2
        const val HEADER_TYPE = 3
    }
}