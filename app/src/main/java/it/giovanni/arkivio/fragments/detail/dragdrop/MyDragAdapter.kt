package it.giovanni.arkivio.fragments.detail.dragdrop

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder

/**
 *  Make the item of RecyclerView draggable.
 *  You have to make your RecyclerViewAdapter implement this interface and also create a instance of
 *  MyDragListener to let your RecyclerView's item draggable.
 */
interface MyDragAdapter {
    /**
     *  Set data of the RecyclerView.
     *  The data could be any type you want, but the list must be mutable.
     *  @param data the underlying data that RecyclerView use it to make a list of items.
     */
    fun setData(data: MutableList<Any>)

    /**
     *  Get data of the RecyclerView.
     *  @return the underlying data that RecyclerView use it to make a list of items.
     */
    fun getData(): MutableList<Any>

    /**
     *  Set the drag listener to handle drag events.
     *  This method MUST called BEFORE setData(); otherwise you'll get a NullPointerException while
     *  calling this method.
     *  @param listener the drag listener that handle all drag events.
     */
    fun setDragListener(listener: MyDragListener)

    /**
     *  Set each item of the RecyclerView to be draggable.
     *  You have to call this method in onBindViewHolder() to set the drag setting on each item's
     *  root layout. All items use the same drag listener.
     *  The "All items" here means not only all items in this RecyclerView, but also items in all
     *  other RecyclerViews which you want its item to be draggable.
     *  And you have to call this method to make the RecyclerView draggable, so that your item won't
     *  fly back to its origin position when receive the ACTION_DROP drag event. (Since we make the
     *  item invisible, the drop event won't be receive when you drop item on the invisible item, so
     *  we need RecyclerView to receive that; otherwise the item will fly back to its original
     *  position due to the drag event return false.)
     *  @param v the View that is going to be set draggable.
     *  @param position the position of this View in the parent RecyclerView.
     *  @param dragListener the drag listener that handle all drag events.
     */
    @SuppressLint("ClickableViewAccessibility")
    fun setDrag(v: View, position: Int, dragListener: MyDragListener) {
        var touchedX = 0f  // closure variable
        var touchedY = 0f  // closure variable
        v.visibility = View.VISIBLE
        v.setOnDragListener(dragListener)
        v.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchedX = event.x
                    touchedY = event.y
                }
            }
            return@setOnTouchListener false  // leave the touch event to other listeners
        }
        v.setOnLongClickListener {
            it.visibility = View.INVISIBLE
            val myShadow = DragShadowBuilder(it)
            it.startDragAndDrop(null, myShadow, it, 0)
        }
    }
}