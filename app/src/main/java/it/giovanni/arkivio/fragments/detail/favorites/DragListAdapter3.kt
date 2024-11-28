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
                    val sourceView = event.localState as View
                    if (!isStarted && sourceView.parent != null) {
                        val sourceRecyclerView: RecyclerView = sourceView.parent as RecyclerView
                        val sourcePosition: Int = sourceRecyclerView.getChildAdapterPosition(sourceView)
                        initPositionInOriParent = sourcePosition
                        finalPosition = sourcePosition
                        isStarted = true
                    }
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    val targetView = event.localState as View
                    if (targetView.parent == null) {
                        return true
                    }
                    val targetRecyclerView: RecyclerView = view.parent as RecyclerView
                    val targetAdapter = targetRecyclerView.adapter!! as DragListAdapter3<T, VH>
                    val targetPosition = targetRecyclerView.getChildAdapterPosition(view)
                    if (view.parent == targetView.parent) {
                        if (isOriginalParent) {
                            try {
                                when (targetRecyclerView.id) {
                                    personalsRecyclerView?.id -> {
                                        Log.i("[FAVORITES]", "1) finalPosition: $finalPosition, targetPosition: $targetPosition")
                                        targetAdapter.notifyItemMoved(finalPosition, targetPosition)
                                    }
                                    availablesRecyclerView?.id -> {
                                    }
                                }
                            } catch (exception: Exception) {
                                println("Ignore IndexOutOfBoundsException: " + exception.message)
                            }
                        } else {
                            /*
                            try {
                                Log.i("[FAVORITES]", "2) finalPositionInOriParent: $finalPositionInOriParent, targetPosition: $targetPosition")
                                targetAdapter.notifyItemMoved(finalPositionInOriParent, targetPosition)
                                // (finalParent?.adapter as DragListAdapter3<T, VH>).getData()!!.removeAt(initPositionInOtherParent)
                                finalParent?.adapter?.notifyDataSetChanged()
                            } catch (exception: Exception) {
                                println("Ignore IndexOutOfBoundsException: " + exception.message)
                            }
                            isOriginalParent = true
                            */
                        }
                        finalPosition = targetPosition
                        finalPositionInOriParent = targetPosition
                    } else {
                        if (isOriginalParent) {
                            Log.i("[FAVORITES]", "2) finalPosition: $finalPosition, targetPosition: $targetPosition")
                            // val sourceValue = ((sourceView.parent as RecyclerView).adapter as DragListAdapter3<T, VH>).getData()[initPositionInOriParent]
                            /*
                            try {
                                // targetAdapter.getData().add(targetPosition, sourceValue)
                                targetAdapter.notifyDataSetChanged()

                                val recyclerView: RecyclerView = (view.parent as RecyclerView)
                                // recyclerView[targetPosition].visibility = View.INVISIBLE // not working, don't know why
                                initPositionInOtherParent = targetPosition
                            } catch (exception: Exception) {
                                println("Ignore IndexOutOfBoundsException: " + exception.message)
                            }
                            */
                            isOriginalParent = false
                            finalPosition = targetPosition
                        } else {
                            Log.i("[FAVORITES]", "3) finalPosition: $finalPosition, targetPosition: $targetPosition")

                            when (targetRecyclerView.id) {
                                personalsRecyclerView?.id -> {
                                    if (finalPosition != targetPosition) {
                                        try {
                                            targetAdapter.notifyItemMoved(finalPosition, targetPosition)
                                        } catch (exception: Exception) {
                                            println("Ignore IndexOutOfBoundsException: " + exception.message)
                                        }
                                        finalPosition = targetPosition
                                    }
                                }
                                availablesRecyclerView?.id -> {
                                }
                            }
                        }
                    }
                    finalParent = view.parent as RecyclerView
                }

                DragEvent.ACTION_DRAG_ENDED -> {
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

    abstract fun onDrop(sourcePosition: Int, targetPosition: Int)
}