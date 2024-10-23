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
import it.giovanni.arkivio.model.favorite.FavoriteUtils
import it.giovanni.arkivio.model.favorite.Personal

class DragAdapter(
    private var availables: MutableList<Personal>,
    private val listener: Listener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), OnTouchListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
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
        val available = availables[position]
        when (holder) {
            is AvailableViewHolder -> holder.bind(available)
            is HeaderViewHolder -> holder.bind(available.availableTitle)
        }
    }

    override fun getItemViewType(position: Int): Int {
        // If the current item is the first one of its group (based on availableTitle), treat it as a header.
        if (position == 0 || availables[position].availableTitle != availables[position - 1].availableTitle) {
            return VIEW_TYPE_HEADER
        }
        return VIEW_TYPE_AVAILABLE
    }

    override fun getItemCount(): Int {
        return availables.size
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

    fun updateAvailableFavorites(list: MutableList<Personal>) {
        this.availables = list
    }

    val dragInstance: DragListener?
        get() = if (listener != null) {
            DragListener(listener)
        } else {
            null
        }

    inner class AvailableViewHolder(private val binding: FavoriteAvailableItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(personal: Personal) {
            binding.title.text = personal.title
            FavoriteUtils.setImageByContentPath(binding.availableImage, personal.images[0].contentPath)
            binding.item.tag = adapterPosition
            binding.item.setOnTouchListener(this@DragAdapter)
            binding.item.setOnDragListener(DragListener(listener))
        }
    }

    inner class HeaderViewHolder(val binding: FavoriteHeaderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerTitle: String?) {
            binding.headerTitle.text = headerTitle
        }
    }

    companion object {
        const val VIEW_TYPE_AVAILABLE = 0
        const val VIEW_TYPE_HEADER = 1
    }
}