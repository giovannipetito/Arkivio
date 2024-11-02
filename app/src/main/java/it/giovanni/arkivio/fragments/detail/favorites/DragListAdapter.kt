package it.giovanni.arkivio.fragments.detail.favorites

import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class DragListAdapter<T, VH : RecyclerView.ViewHolder>(
    diffUtil: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffUtil) {

    val dragListener = object : View.OnDragListener {
        override fun onDrag(view: View?, event: DragEvent?): Boolean {
            event?.let {
                when (it.action) {
                    DragEvent.ACTION_DRAG_ENTERED -> {
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                    }
                    DragEvent.ACTION_DROP -> {

                        val sourceView = it.localState as View
                        val sourceRecyclerView = sourceView.parent as RecyclerView
                        val sourceAdapter = sourceRecyclerView.adapter as DragListAdapter<T, VH>
                        val sourcePosition = sourceRecyclerView.getChildAdapterPosition(sourceView)

                        view?.let { targetView ->
                            var targetRecyclerView: RecyclerView? = targetView as? RecyclerView

                            if (targetRecyclerView == null) {
                                targetRecyclerView = targetView.parent as? RecyclerView
                            }

                            if (targetRecyclerView !is RecyclerView) {
                                return false
                            }

                            val targetAdapter = targetRecyclerView.adapter as DragListAdapter<T, VH>
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
                                        sourceAdapter.onSet(targetPosition, sourcePosition)
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

    abstract fun onSet(targetIndex: Int, sourceIndex: Int)

    abstract fun onSwap(from: Int, to: Int)
}