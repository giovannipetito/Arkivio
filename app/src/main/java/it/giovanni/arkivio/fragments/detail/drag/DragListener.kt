package it.giovanni.arkivio.fragments.detail.drag

import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.detail.dragdrop.Favorite

class DragListener(private val listener: Listener?) : OnDragListener {

    private var isDropped: Boolean = false

    override fun onDrag(view: View, event: DragEvent): Boolean {

        if (event.action == DragEvent.ACTION_DRAG_STARTED) {
            Log.i("[DRAGDROP]", "ACTION_DRAG_STARTED")
        }

        if (event.action == DragEvent.ACTION_DRAG_ENTERED) {
            Log.i("[DRAGDROP]", "ACTION_DRAG_ENTERED")
        }

        if (event.action == DragEvent.ACTION_DRAG_LOCATION) {
            // Log.i("[DRAGDROP]", "ACTION_DRAG_LOCATION")
        }

        if (event.action == DragEvent.ACTION_DROP) {
            Log.i("[DRAGDROP]", "ACTION_DROP")
        }

        if (event.action == DragEvent.ACTION_DRAG_ENDED) {
            Log.i("[DRAGDROP]", "ACTION_DRAG_ENDED")
        }

        if (event.action == DragEvent.ACTION_DRAG_EXITED) {
            Log.i("[DRAGDROP]", "ACTION_DRAG_EXITED")
        }

        if (event.action == DragEvent.ACTION_DROP) {

            isDropped = true
            var positionTarget: Int = -1

            val viewSource: View = event.localState as View
            val viewId: Int = view.id
            val dragIcon: Int = R.id.item

            val topListContainer: Int = R.id.top_list_container
            val bottomListContainer: Int = R.id.bottom_list_container

            val topList: Int = R.id.top_list
            val bottomList: Int = R.id.bottom_list

            val target: RecyclerView

            if (viewId == dragIcon || viewId == topListContainer || viewId == bottomListContainer || viewId == topList || viewId == bottomList) {
                when (viewId) {
                    topListContainer, topList -> {
                        target = view.rootView.findViewById(topList)
                    }
                    bottomListContainer, bottomList -> {
                        target = view.rootView.findViewById(bottomList)
                    }
                    else -> {
                        target = view.parent as RecyclerView
                        positionTarget = view.tag as Int
                    }
                }

                val source = viewSource.parent as RecyclerView

                val positionSource = viewSource.tag as Int
                val sourceId = source.id

                val sourceAdapter = source.adapter as DragAdapter?
                val targetAdapter = target.adapter as DragAdapter?

                if (sourceAdapter != null && targetAdapter != null) {
                    val favorite: Favorite = sourceAdapter.list[positionSource]
                    val sourceList: MutableList<Favorite> = sourceAdapter.list
                    val targetList: MutableList<Favorite> = targetAdapter.list

                    sourceList.removeAt(positionSource)
                    sourceAdapter.updateList(sourceList)
                    sourceAdapter.notifyDataSetChanged()

                    if (positionTarget >= 0) {
                        targetList.add(positionTarget, favorite)
                    } else {
                        targetList.add(favorite)
                    }

                    targetAdapter.updateList(targetList)
                    targetAdapter.notifyDataSetChanged()

                    if ((sourceId == bottomList && sourceAdapter.itemCount < 1) || viewId == bottomListContainer) {
                        listener?.notifyBottomListEmpty()
                    }

                    if ((sourceId == topList && sourceAdapter.itemCount < 1) || viewId == topListContainer) {
                        listener?.notifyTopListEmpty()
                    }
                }
            }
        }

        /*
        if (!isDropped && event.localState != null) {
            (event.localState as View).visibility = View.VISIBLE
        }
        */

        return true
    }
}