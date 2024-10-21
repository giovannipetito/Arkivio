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
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R

class MainAdapter(
    var list: MutableList<String>,
    private val listener: Listener?
) : RecyclerView.Adapter<MainAdapter.ListViewHolder>(), OnTouchListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.drag_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.dragIcon.tag = position
        holder.dragIcon.setOnTouchListener(this)
        holder.dragIcon.setOnDragListener(DragListener(listener))
        holder.dragTitle.text = list[position]
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

    fun updateList(list: MutableList<String>) {
        this.list = list
    }

    val dragInstance: DragListener?
        get() = if (listener != null) {
            DragListener(listener)
        } else {
            null
        }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dragIcon: RelativeLayout = itemView.findViewById(R.id.drag_icon)
        var dragTitle: TextView = itemView.findViewById(R.id.drag_title)
    }
}