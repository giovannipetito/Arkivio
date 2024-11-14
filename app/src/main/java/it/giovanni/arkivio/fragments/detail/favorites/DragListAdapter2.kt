package it.giovanni.arkivio.fragments.detail.favorites

import android.view.View
import android.view.DragEvent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class DragListAdapter2<T, VH : RecyclerView.ViewHolder>(diffUtil: DiffUtil.ItemCallback<T>) : ListAdapter<T, VH>(diffUtil) {

    private var isDragStarted = false
    private var isOriginalParent = true
    private var initialPositionInOriginalParent = 0
    private var initialPositionInOtherParent = 0
    private var finalPosition = 0
    private var finalPositionInOriginalParent = 0
    private var finalParent: RecyclerView? = null

    val dragListener = object : View.OnDragListener {
        override fun onDrag(view: View?, event: DragEvent?): Boolean {
            event?.let {
                when (it.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        val sourceView = event.localState as? View ?: return true
                        if (!isDragStarted && sourceView.parent is RecyclerView) {
                            val sourceRecyclerView = sourceView.parent as RecyclerView
                            initialPositionInOriginalParent = sourceRecyclerView.getChildAdapterPosition(sourceView)
                            finalPosition = initialPositionInOriginalParent
                            isDragStarted = true
                        }
                    }
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        val sourceView = event.localState as? View ?: return true
                        val targetRecyclerView = (view?.parent as? RecyclerView) ?: return true
                        val targetAdapter = targetRecyclerView.adapter as? DragListAdapter2<T, VH> ?: return true
                        val targetPosition = targetRecyclerView.getChildAdapterPosition(view)

                        if (view.parent == sourceView.parent) {
                            if (isOriginalParent) {
                                try {
                                    targetAdapter.notifyItemMoved(finalPosition, targetPosition)
                                } catch (e: Exception) {
                                    println("ignore index out of bound: ${e.message}")
                                }
                            } else {
                                try {
                                    targetAdapter.notifyItemMoved(finalPositionInOriginalParent, targetPosition)
                                    (finalParent?.adapter as? DragListAdapter2<T, VH>)?.currentList?.removeAt(initialPositionInOtherParent)
                                    finalParent?.adapter?.notifyDataSetChanged()
                                } catch (e: Exception) {
                                    println("ignore index out of bound: ${e.message}")
                                }
                                isOriginalParent = true
                            }
                            finalPosition = targetPosition
                            finalPositionInOriginalParent = targetPosition
                        } else {
                            if (isOriginalParent) {
                                val sourceValue = (sourceView.parent as RecyclerView).adapter?.let { adapter ->
                                    (adapter as DragListAdapter2<T, VH>).currentList[initialPositionInOriginalParent]
                                } ?: return true

                                try {
                                    targetAdapter.currentList.add(targetPosition, sourceValue)
                                    targetAdapter.notifyDataSetChanged()
                                    initialPositionInOtherParent = targetPosition
                                } catch (e: Exception) {
                                    println("ignore index out of bound: ${e.message}")
                                }
                                isOriginalParent = false
                                finalPosition = targetPosition
                            } else if (finalPosition != targetPosition) {
                                try {
                                    targetAdapter.notifyItemMoved(finalPosition, targetPosition)
                                } catch (e: Exception) {
                                    println("ignore index out of bound: ${e.message}")
                                }
                                finalPosition = targetPosition
                            }
                        }
                        finalParent = targetRecyclerView
                    }
                }
            }
            return true
        }
    }

    // Abstract functions to handle swap and drag location behavior
    abstract fun onDragLocation(showBadge: Boolean)
    abstract fun onSet(targetPosition: Int, sourcePosition: Int)
    abstract fun onSwap(sourcePosition: Int, targetPosition: Int)
}