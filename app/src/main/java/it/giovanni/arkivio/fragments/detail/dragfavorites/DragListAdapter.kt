package it.giovanni.arkivio.fragments.detail.dragfavorites

import android.util.Log
import android.view.View
import android.view.DragEvent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class DragListAdapter<T, VH : RecyclerView.ViewHolder>(diffUtil: DiffUtil.ItemCallback<T>) : ListAdapter<T, VH>(diffUtil) {

    private var sourceView: View? = null
    private var sourceRecyclerView: RecyclerView? = null

    private var isSourceView = true
    private var finalPosition = 0
    private var finalParent: RecyclerView? = null

    val dragListener = object : View.OnDragListener {
        override fun onDrag(view: View?, event: DragEvent?): Boolean {
            if (view == null || view is RecyclerView || view.parent == null) {
                return true
            }
            when (event?.action) {

                DragEvent.ACTION_DRAG_STARTED -> {
                    sourceView = event.localState as View
                    sourceRecyclerView = sourceView?.parent as RecyclerView?
                    val sourcePosition: Int? = sourceRecyclerView?.getChildAdapterPosition(sourceView!!)
                    finalPosition = sourcePosition!!
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    val targetView = event.localState as View
                    if (targetView.parent == null) {
                        return true
                    }
                    val targetRecyclerView: RecyclerView = view.parent as RecyclerView
                    val targetAdapter = targetRecyclerView.adapter!! as DragListAdapter<T, VH>
                    val targetPosition = targetRecyclerView.getChildAdapterPosition(view)
                    if (view.parent == targetView.parent) {
                        if (isSourceView) {
                            try {
                                Log.i("[FAVORITES]", "1) finalPosition: $finalPosition, targetPosition: $targetPosition")
                                if (sourceRecyclerView?.id == personalsRecyclerView?.id && targetRecyclerView.id == personalsRecyclerView?.id) {
                                    targetAdapter.notifyItemMoved(finalPosition, targetPosition)
                                }
                            } catch (exception: Exception) {
                                println("Ignore IndexOutOfBoundsException: " + exception.message)
                            }
                        } else {
                            Log.i("[FAVORITES]", "2) targetPosition: $targetPosition")
                        }
                        finalPosition = targetPosition
                    } else {
                        if (isSourceView) {
                            Log.i("[FAVORITES]", "3) finalPosition: $finalPosition, targetPosition: $targetPosition")
                            isSourceView = false
                            finalPosition = targetPosition
                        } else {
                            Log.i("[FAVORITES]", "4) finalPosition: $finalPosition, targetPosition: $targetPosition")
                            if (finalPosition != targetPosition) {
                                try {
                                    if (sourceRecyclerView?.id == availablesRecyclerView?.id && targetRecyclerView.id == personalsRecyclerView?.id) {
                                        targetAdapter.notifyItemMoved(finalPosition, targetPosition)
                                    }
                                } catch (exception: Exception) {
                                    println("Ignore IndexOutOfBoundsException: " + exception.message)
                                }
                                finalPosition = targetPosition
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
                    if (finalParent == sourceView.parent) {
                        Log.i("[FAVORITES]", "5) finalPosition: $finalPosition")
                    } else {
                        Log.i("[FAVORITES]", "6) finalPosition: $finalPosition")
                    }
                    (finalParent!!.adapter as DragListAdapter<T, VH>).notifyDataSetChanged() // TODO: TO REMOVE?
                    (sourceView.parent as RecyclerView?)?.adapter?.notifyDataSetChanged()  // TODO: TO REMOVE?
                    finalParent = null
                    isSourceView = true
                }
            }
            return true
        }
    }

    abstract fun onDragLocation(showBadge: Boolean)

    abstract fun onSwap(sourcePosition: Int, targetPosition: Int)

    abstract fun onDrop(sourcePosition: Int, targetPosition: Int)
}