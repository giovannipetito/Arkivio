package it.giovanni.arkivio.fragments.detail.drop

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R

class DropAdapter : RecyclerView.Adapter<DropAdapter.MyViewHolder>() {

    private var clickListener: OnClickListener? = null
    private var myDataset = mutableListOf<Any>()
    private var dragListener: DropListener? = null

    interface OnClickListener {
        fun recyclerviewClick(name: String)
    }

    fun setClickListener(parentFragment: OnClickListener) {
        clickListener = parentFragment
    }

    fun setDragListener(listener: DropListener) {
        dragListener = listener
    }

    fun setData(data: MutableList<Any>) {
        myDataset = data
        notifyDataSetChanged()
    }

    fun getData(): MutableList<Any> {
        return myDataset
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        // create a new view
        val item = LayoutInflater.from(parent.context).inflate(R.layout.drop_item, parent, false) as View
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(item)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name = myDataset[position]
        holder.txtAnimalName.text = name as String
        holder.layoutAnimal.setOnClickListener {
            clickListener?.recyclerviewClick(name)
        }
        setDrag(holder.layoutAnimal, dragListener!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setDrag(v: View, dragListener: DropListener) {
        v.visibility = View.VISIBLE
        v.setOnDragListener(dragListener)
        v.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                }
            }
            return@setOnTouchListener false  // leave the touch event to other listeners
        }
        v.setOnLongClickListener {
            it.visibility = View.INVISIBLE
            val shadowBuilder = DragShadowBuilder(it)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(
                    null,
                    shadowBuilder,
                    it,
                    0
                )
            } else {
                it.startDrag(null, shadowBuilder, it, 0)
            }
        }
    }

    /**
     *  This is important, set a drag listener to the RecyclerView to
     *  receive the ACTION_DROP drag event.
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setOnDragListener(dragListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        clickListener = null
        clear()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    fun clear() {
        myDataset = mutableListOf()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutAnimal: RelativeLayout = itemView.findViewById(R.id.layoutAnimal)
        val txtAnimalName: TextView = itemView.findViewById(R.id.txtAnimalName)
    }
}