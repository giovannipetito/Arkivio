package it.giovanni.arkivio.fragments.detail.dragdrop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.TextViewCustom

class MyRecyclerviewAdapter : RecyclerView.Adapter<MyRecyclerviewAdapter.MyViewHolder>(), MyDragAdapter {

    private var clickListener: OnClickListener? = null
    private var myDataset = mutableListOf<Any>()
    private var dragListener: MyDragListener? = null

    interface OnClickListener {
        fun recyclerviewClick(name: String)
    }

    fun setClickListener(parentFragment: OnClickListener) {
        clickListener = parentFragment
    }

    override fun setDragListener(listener: MyDragListener) {
        dragListener = listener
    }

    override fun setData(data: MutableList<Any>) {
        myDataset = data
        notifyDataSetChanged()
    }

    override fun getData(): MutableList<Any> {
        return myDataset
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animalItem: RelativeLayout = itemView.findViewById(R.id.animal_item)
        val animalText: TextViewCustom = itemView.findViewById(R.id.animal_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.drag_item, parent, false) as View
        return MyViewHolder(item)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name = myDataset[position]
        holder.animalText.text = name as String
        holder.animalItem.setOnClickListener {
            clickListener?.recyclerviewClick(name)
        }
        setDrag(holder.animalItem, position,dragListener!!)
    }

    /**
     *  This is important, set a drag listener to the RecyclerView to receive the ACTION_DROP drag event.
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

    override fun getItemCount() = myDataset.size

    fun clear() {
        myDataset = mutableListOf<Any>()
    }
}