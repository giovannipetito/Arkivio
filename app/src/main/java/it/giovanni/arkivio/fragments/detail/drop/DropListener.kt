package it.giovanni.arkivio.fragments.detail.drop

import android.view.DragEvent
import android.view.View
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView

/**
 *  MyDragListener is the listener that handle all drag events.
 *  It should be used with MyDraggableRecyclerviewAdaptor.
 *  All items that is draggable should set the same drag listener instance
 *  as their drag listener, in order to use the same variables in the instance.
 */
class DropListener : View.OnDragListener {

    private var isStarted = false
    private var isOriginalParent = true
    private var initPositionInOriParent = 0
    private var initPositionInOtherParent = 0
    private var finalPosition = 0
    private var finalPositionInOriParent = 0
    private var finalParent: RecyclerView? = null

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        if (v == null || v is RecyclerView || v.parent == null) {
            return true
        }
        when (event?.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                val sourceView = event.localState as View
                if (!isStarted && sourceView.parent != null) {
                    val sourcePosition = (sourceView.parent as RecyclerView).getChildAdapterPosition(sourceView)
                    initPositionInOriParent = sourcePosition
                    finalPosition = sourcePosition
                    isStarted = true
                }
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                val sourceView = event.localState as View
                if (sourceView.parent == null) {
                    return true
                }
                val targetAdapter = (v.parent as RecyclerView).adapter!! as DropAdapter
                val targetPosition = (v.parent as RecyclerView).getChildAdapterPosition(v)
                if (v.parent == sourceView.parent) {
                    if (isOriginalParent) {
                        try {
                            targetAdapter.notifyItemMoved(finalPosition, targetPosition)
                        } catch (e: Exception) {
                            println("ignore index out of bound")
                        }
                    } else {
                        try {
                            targetAdapter.notifyItemMoved(finalPositionInOriParent, targetPosition)
                            (finalParent?.adapter as DropAdapter?)?.getData()!!.removeAt(initPositionInOtherParent)
                            finalParent?.adapter?.notifyDataSetChanged()
                        } catch (e: Exception) {
                            println("ignore index out of bound")
                        }
                        isOriginalParent = true
                    }
                    finalPosition = targetPosition
                    finalPositionInOriParent = targetPosition
                } else {
                    if (isOriginalParent) {
                        val sourceValue = ((sourceView.parent as RecyclerView).adapter as DropAdapter).getData()[initPositionInOriParent]
                        try {
                            targetAdapter.getData().add(targetPosition, sourceValue)
                            targetAdapter.notifyDataSetChanged()
                            (v.parent as RecyclerView)[targetPosition].visibility = View.INVISIBLE  // not working, don't know why
                            initPositionInOtherParent = targetPosition
                        } catch (e: Exception) {
                            println("ignore index out of bound")
                        }
                        isOriginalParent = false
                        finalPosition = targetPosition
                    } else {
                        if (finalPosition != targetPosition) {
                            try {
                                targetAdapter.notifyItemMoved(finalPosition, targetPosition)
                            } catch (e: Exception) {
                                println("ignore index out of bound")
                            }
                            finalPosition = targetPosition
                        }
                    }
                }
                finalParent = v.parent as RecyclerView
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                val sourceView = event.localState as View
                if (finalParent == null || sourceView.parent == null) {
                    return true
                }
                val sourceParent = sourceView.parent as RecyclerView
                val sourceValue = ((sourceView.parent as RecyclerView).adapter as DropAdapter).getData()[initPositionInOriParent]
                if (finalParent == sourceView.parent) {
                    (finalParent!!.adapter as DropAdapter).getData().removeAt(initPositionInOriParent)
                    (finalParent!!.adapter as DropAdapter).getData().add(finalPosition, sourceValue)
                } else {
                    (sourceParent.adapter as DropAdapter).getData().removeAt(initPositionInOriParent)
                    (finalParent!!.adapter as DropAdapter).getData().removeAt(initPositionInOtherParent)
                    (finalParent!!.adapter as DropAdapter).getData().add(finalPosition, sourceValue)
                }
                (finalParent!!.adapter as DropAdapter).notifyDataSetChanged()
                (sourceView.parent as RecyclerView?)?.adapter?.notifyDataSetChanged()
                isStarted = false
                finalParent = null
                isOriginalParent = true
            }
        }
        return true
    }
}