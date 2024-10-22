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
import it.giovanni.arkivio.databinding.FavoritePersonalItemBinding
import it.giovanni.arkivio.fragments.detail.dragdrop.Favorite

class DragAdapter(
    var list: MutableList<Favorite>,
    private val listener: Listener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), OnTouchListener {

    inner class PersonalViewHolder(private val binding: FavoritePersonalItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: Favorite) {
            binding.title.text = favorite.title
            binding.item.tag = adapterPosition // position
            binding.item.setOnTouchListener(this@DragAdapter)
            binding.item.setOnDragListener(DragListener(listener))
        }
    }

    inner class AvailableViewHolder(private val binding: FavoriteAvailableItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: Favorite) {
            binding.title.text = favorite.title
            binding.item.tag = adapterPosition // position
            binding.item.setOnTouchListener(this@DragAdapter)
            binding.item.setOnDragListener(DragListener(listener))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].isPersonal)
            PERSONAL_TYPE
        else
            AVAILABLE_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == PERSONAL_TYPE) {
            val binding = FavoritePersonalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            PersonalViewHolder(binding)
        } else {
            val binding = FavoriteAvailableItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AvailableViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val favorite = list[position]

        if (holder is PersonalViewHolder) {
            holder.bind(favorite)
        } else if (holder is AvailableViewHolder) {
            holder.bind(favorite)
        }
    }

    override fun getItemCount(): Int {
        return list.size
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

    fun updateList(list: MutableList<Favorite>) {
        this.list = list
    }

    val dragInstance: DragListener?
        get() = if (listener != null) {
            DragListener(listener)
        } else {
            null
        }

    companion object {
        private const val PERSONAL_TYPE = 1
        private const val AVAILABLE_TYPE = 2
    }
}