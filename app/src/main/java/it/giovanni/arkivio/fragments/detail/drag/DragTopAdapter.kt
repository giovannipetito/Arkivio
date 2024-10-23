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
import it.giovanni.arkivio.databinding.FavoritePersonalItemBinding
import it.giovanni.arkivio.model.favorite.FavoriteUtils
import it.giovanni.arkivio.model.favorite.Personal

class DragTopAdapter(
    private var personals: MutableList<Personal>,
    private val listener: Listener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), OnTouchListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = FavoritePersonalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PersonalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val personal = personals[position]
        when (holder) {
            is PersonalViewHolder -> holder.bind(personal)
        }
    }

    override fun getItemCount(): Int {
        return personals.size
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

    val dragInstance: DragListener?
        get() = if (listener != null) {
            DragListener(listener)
        } else {
            null
        }

    inner class PersonalViewHolder(private val binding: FavoritePersonalItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(personal: Personal) {
            binding.title.text = personal.title
            FavoriteUtils.setImageByContentPath(binding.personalImage, personal.images[0].contentPath)
            binding.item.tag = adapterPosition
            binding.item.setOnTouchListener(this@DragTopAdapter)
            binding.item.setOnDragListener(DragListener(listener))
        }
    }
}