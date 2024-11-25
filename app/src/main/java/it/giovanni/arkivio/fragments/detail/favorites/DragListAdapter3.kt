package it.giovanni.arkivio.fragments.detail.favorites

import android.util.Log
import android.view.View
import android.view.DragEvent
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class DragListAdapter3<T, VH : RecyclerView.ViewHolder>(diffUtil: DiffUtil.ItemCallback<T>) : ListAdapter<T, VH>(diffUtil) {

    private var isStarted = false
    private var isOriginalParent = true
    private var initPositionInOriParent = 0
    private var initPositionInOtherParent = 0
    private var finalPosition = 0
    private var finalPositionInOriParent = 0
    private var finalParent: RecyclerView? = null

    val dragListener = object : View.OnDragListener {
        override fun onDrag(view: View?, event: DragEvent?): Boolean {
            if (view == null || view is RecyclerView || view.parent == null) {
                return true
            }
            when (event?.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Log.i("[FAVORITES]", "ACTION_DRAG_STARTED")
                    val sourceView = event.localState as View
                    if (!isStarted && sourceView.parent != null) {
                        val sourcePosition = (sourceView.parent as RecyclerView).getChildAdapterPosition(sourceView)
                        initPositionInOriParent = sourcePosition
                        finalPosition = sourcePosition
                        isStarted = true
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    Log.i("[FAVORITES]", "ACTION_DRAG_ENTERED")
                    val sourceView = event.localState as View
                    if (sourceView.parent == null) {
                        return true
                    }
                    val targetAdapter = (view.parent as RecyclerView).adapter!! as DragListAdapter3<T, VH>
                    val targetPosition = (view.parent as RecyclerView).getChildAdapterPosition(view)
                    if (view.parent == sourceView.parent) {
                        if (isOriginalParent) {
                            try {
                                targetAdapter.notifyItemMoved(finalPosition, targetPosition)
                            } catch (e: Exception) {
                                println("ignore index out of bound")
                            }
                        } else {
                            try {
                                targetAdapter.notifyItemMoved(finalPositionInOriParent, targetPosition)
                                // (finalParent?.adapter as DragListAdapter3<T, VH>).getData()!!.removeAt(initPositionInOtherParent)
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
                            // val sourceValue = ((sourceView.parent as RecyclerView).adapter as DragListAdapter3<T, VH>).getData()[initPositionInOriParent]
                            try {
                                // targetAdapter.getData().add(targetPosition, sourceValue)
                                targetAdapter.notifyDataSetChanged()

                                val recyclerView: RecyclerView = (view.parent as RecyclerView)
                                recyclerView[targetPosition].visibility = View.INVISIBLE  // not working, don't know why
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
                                    println("ignore index out of bound: " + e.message)
                                }
                                finalPosition = targetPosition
                            }
                        }
                    }
                    finalParent = view.parent as RecyclerView
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    Log.i("[FAVORITES]", "ACTION_DRAG_ENDED")
                    val sourceView = event.localState as View
                    if (finalParent == null || sourceView.parent == null) {
                        return true
                    }
                    val sourceParent = sourceView.parent as RecyclerView
                    // val sourceValue = ((sourceView.parent as RecyclerView).adapter as DragListAdapter3<T, VH>).getData()[initPositionInOriParent]
                    if (finalParent == sourceView.parent) {
                        // (finalParent!!.adapter as DragListAdapter3<T, VH>).getData().removeAt(initPositionInOriParent)
                        // (finalParent!!.adapter as DragListAdapter3<T, VH>).getData().add(finalPosition, sourceValue)
                    } else {
                        // (sourceParent.adapter as DragListAdapter3<T, VH>).getData().removeAt(initPositionInOriParent)
                        // (finalParent!!.adapter as DragListAdapter3<T, VH>).getData().removeAt(initPositionInOtherParent)
                        // (finalParent!!.adapter as DragListAdapter3<T, VH>).getData().add(finalPosition, sourceValue)
                    }
                    (finalParent!!.adapter as DragListAdapter3<T, VH>).notifyDataSetChanged()
                    (sourceView.parent as RecyclerView?)?.adapter?.notifyDataSetChanged()
                    isStarted = false
                    finalParent = null
                    isOriginalParent = true
                }
            }
            return true
        }
    }

    abstract fun onDragLocation(showBadge: Boolean)

    abstract fun onSwap(sourcePosition: Int, targetPosition: Int)

    // abstract fun onDrag(sourcePosition: Int, targetPosition: Int)

    abstract fun onDrop(sourcePosition: Int, targetPosition: Int)

    abstract fun onSwapEntered(sourcePosition: Int, targetPosition: Int)
    abstract fun onSwapEnded(sourcePosition: Int, targetPosition: Int)
    abstract fun onDragEntered(sourcePosition: Int, targetPosition: Int)
    abstract fun onDragEnded(sourcePosition: Int, targetPosition: Int)
}