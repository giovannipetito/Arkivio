package it.giovanni.arkivio.fragments.detail.favorites

import android.util.Log
import android.view.View
import android.view.DragEvent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class DragListAdapter2<T, VH : RecyclerView.ViewHolder>(diffUtil: DiffUtil.ItemCallback<T>) : ListAdapter<T, VH>(diffUtil) {

    private lateinit var sourceView: View
    private lateinit var sourceRecyclerView: RecyclerView
    private lateinit var sourceAdapter: DragListAdapter2<T, VH>
    private var sourcePosition: Int = -1

    private var lastLoggedTargetPosition: Int = -1
    private var lastLoggedSourcePosition: Int = -1

    val dragListener = object : View.OnDragListener {
        override fun onDrag(view: View?, event: DragEvent?): Boolean {
            event?.let {
                when (it.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        // Retrieve the source view and calculate sourcePosition
                        sourceView = it.localState as View
                        if (sourceView.parent != null) {
                            sourceRecyclerView = sourceView.parent as RecyclerView
                            sourceAdapter = sourceRecyclerView.adapter as DragListAdapter2<T, VH>
                            sourcePosition = sourceRecyclerView.getChildAdapterPosition(sourceView)
                        }
                    }

                    DragEvent.ACTION_DRAG_LOCATION -> {
                        view?.let { targetView ->
                            val targetRecyclerView = when {
                                targetView.parent is RecyclerView -> targetView.parent as RecyclerView
                                targetView is RecyclerView -> targetView
                                else -> null
                            }

                            if (targetRecyclerView != null) {
                                when (targetRecyclerView.id) {
                                    personalsRecyclerView?.id -> {

                                        try {
                                            var targetPosition = targetRecyclerView.getChildAdapterPosition(targetView)

                                            // Log only if targetPosition or sourcePosition has changed
                                            if (targetPosition != lastLoggedTargetPosition || sourcePosition != lastLoggedSourcePosition) {
                                                lastLoggedTargetPosition = targetPosition
                                                lastLoggedSourcePosition = sourcePosition
                                                Log.i("[FAVORITES]", "targetPosition: $targetPosition")
                                                Log.i("[FAVORITES]", "sourcePosition: $sourcePosition")
                                                val targetAdapter = targetRecyclerView.adapter as DragListAdapter2<T, VH>
                                                // targetAdapter.notifyItemMoved(sourcePosition, targetPosition)

                                                // If the recyclerview is the same, swap two items.
                                                if (sourceRecyclerView.id == targetRecyclerView.id) {
                                                    if (targetPosition >= 0 && sourceAdapter.currentList[targetPosition] != null) {
                                                        // sourceAdapter.onSwap(sourcePosition, targetPosition)
                                                    }
                                                }
                                            }
                                        } catch (e: ClassCastException) {
                                            e.printStackTrace()
                                        }

                                        onDragLocation(showBadge = false)
                                    }
                                    availablesRecyclerView?.id -> {
                                        onDragLocation(showBadge = true)
                                    }
                                }
                            }
                        }
                    }

                    DragEvent.ACTION_DROP -> {

                        val sourceView = it.localState as View
                        val sourceRecyclerView = sourceView.parent as RecyclerView
                        val sourceAdapter = sourceRecyclerView.adapter as DragListAdapter2<T, VH>
                        val sourcePosition = sourceRecyclerView.getChildAdapterPosition(sourceView)

                        view?.let { targetView ->
                            var targetRecyclerView: RecyclerView? = targetView as? RecyclerView

                            if (targetRecyclerView == null) {
                                targetRecyclerView = targetView.parent as? RecyclerView
                            }

                            if (targetRecyclerView !is RecyclerView) {
                                return false
                            }

                            val targetAdapter = targetRecyclerView.adapter as DragListAdapter2<T, VH>
                            val targetPosition =
                                if (targetView is RecyclerView) {
                                    if (sourceRecyclerView.id == targetRecyclerView.id)
                                        -1 // we can't swap in empty area.
                                    else 0 // we can swap in empty area.
                                }
                                else targetRecyclerView.getChildAdapterPosition(targetView)

                            // If the recyclerview is the same, swap two items.
                            if (sourceRecyclerView.id == targetRecyclerView.id) {
                                if (targetPosition >= 0 && sourceAdapter.currentList[targetPosition] != null) {
                                    sourceAdapter.onSwap(sourcePosition, targetPosition)
                                }
                            } else {
                                try {
                                    targetAdapter.currentList[targetPosition]?.let {
                                        sourceAdapter.onDrop(sourcePosition, targetPosition)
                                    }
                                } catch (e: IndexOutOfBoundsException) {
                                    println(e.message)
                                }
                            }
                        } ?: run {
                            return false
                        }
                    }
                }
            }
            return true
        }
    }

    abstract fun onDragLocation(showBadge: Boolean)

    abstract fun onSwap(sourcePosition: Int, targetPosition: Int)

    abstract fun onDrop(sourcePosition: Int, targetPosition: Int)
}