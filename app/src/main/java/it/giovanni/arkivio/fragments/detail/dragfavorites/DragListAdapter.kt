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
                    val targetAdapter = targetRecyclerView.adapter as DragListAdapter<T, VH>
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
                    finalParent = null
                    isSourceView = true
                }
            }
            return true
        }
    }
}

// Solution 1: Using ACTION_DRAG_ENTERED
abstract class DragListAdapter1<T, VH : RecyclerView.ViewHolder>(diffUtil: DiffUtil.ItemCallback<T>) : ListAdapter<T, VH>(diffUtil) {
    private var sourceView: View? = null
    private var sourceRecyclerView: RecyclerView? = null
    private var isSourceView = true
    private var finalPosition = 0
    private var finalParent: RecyclerView? = null

    val dragListener = object : View.OnDragListener {
        override fun onDrag(view: View?, event: DragEvent?): Boolean {
            if (view == null || event == null) return true

            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    sourceView = event.localState as View
                    sourceRecyclerView = sourceView?.parent as RecyclerView?
                    finalPosition = sourceRecyclerView?.getChildAdapterPosition(sourceView!!) ?: 0
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    val targetView = view
                    val targetRecyclerView = view.parent as? RecyclerView ?: return true
                    val targetPosition = targetRecyclerView.getChildAdapterPosition(view)

                    if (targetRecyclerView == sourceRecyclerView) {
                        // Handle reordering within the same RecyclerView
                        if (isSourceView && finalPosition != targetPosition) {
                            try {
                                if (sourceRecyclerView?.id == personalsRecyclerView?.id) {
                                    onDrag(true, finalPosition, targetPosition)
                                    notifyItemMoved(finalPosition, targetPosition)
                                }
                            } catch (exception: Exception) {
                                println("Ignore IndexOutOfBoundsException: ${exception.message}")
                            }
                        }
                    } else {
                        // Handle moving between different RecyclerViews
                        if (!isSourceView && finalPosition != targetPosition) {
                            try {
                                if (sourceRecyclerView?.id == availablesRecyclerView?.id &&
                                    targetRecyclerView.id == personalsRecyclerView?.id) {
                                    onDrag(false, finalPosition, targetPosition)
                                    notifyItemMoved(finalPosition, targetPosition)
                                }
                            } catch (exception: Exception) {
                                println("Ignore IndexOutOfBoundsException: ${exception.message}")
                            }
                        }
                        isSourceView = false
                    }
                    finalPosition = targetPosition
                    finalParent = targetRecyclerView
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    finalParent = null
                    isSourceView = true
                }
            }
            return true
        }
    }

    abstract fun onDrag(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int)
}

// Solution 2: Using ACTION_DRAG_ENDED
abstract class DragListAdapter2<T, VH : RecyclerView.ViewHolder>(diffUtil: DiffUtil.ItemCallback<T>) : ListAdapter<T, VH>(diffUtil) {
    private var sourceView: View? = null
    private var sourceRecyclerView: RecyclerView? = null
    private var sourcePosition = 0
    private var lastTargetPosition = 0
    private var lastTargetRecyclerView: RecyclerView? = null

    val dragListener = object : View.OnDragListener {
        override fun onDrag(view: View?, event: DragEvent?): Boolean {
            if (view == null || event == null) return true

            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    sourceView = event.localState as View
                    sourceRecyclerView = sourceView?.parent as RecyclerView?
                    sourcePosition = sourceRecyclerView?.getChildAdapterPosition(sourceView!!) ?: 0
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    val targetRecyclerView = view.parent as? RecyclerView ?: return true
                    lastTargetPosition = targetRecyclerView.getChildAdapterPosition(view)
                    lastTargetRecyclerView = targetRecyclerView
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    if (lastTargetRecyclerView != null) {
                        val isPersonal = sourceRecyclerView?.id == personalsRecyclerView?.id

                        if (sourceRecyclerView == lastTargetRecyclerView) {
                            // Handle reordering within the same RecyclerView
                            if (sourcePosition != lastTargetPosition) {
                                try {
                                    if (sourceRecyclerView?.id == personalsRecyclerView?.id) {
                                        onDrag(true, sourcePosition, lastTargetPosition)
                                        notifyItemMoved(sourcePosition, lastTargetPosition)
                                    }
                                } catch (exception: Exception) {
                                    println("Ignore IndexOutOfBoundsException: ${exception.message}")
                                }
                            }
                        } else {
                            // Handle moving between different RecyclerViews
                            try {
                                if (sourceRecyclerView?.id == availablesRecyclerView?.id &&
                                    lastTargetRecyclerView?.id == personalsRecyclerView?.id) {
                                    onDrag(false, sourcePosition, lastTargetPosition)
                                    notifyItemMoved(sourcePosition, lastTargetPosition)
                                }
                            } catch (exception: Exception) {
                                println("Ignore IndexOutOfBoundsException: ${exception.message}")
                            }
                        }
                    }
                    // Reset values
                    sourceView = null
                    lastTargetRecyclerView = null
                    lastTargetPosition = 0
                }
            }
            return true
        }
    }

    abstract fun onDrag(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int)
}