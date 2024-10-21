package it.giovanni.arkivio.fragments.detail.drag

import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R

class DragListener(private val listener: Listener?) : OnDragListener {

    private var isDropped: Boolean = false

    override fun onDrag(view: View, event: DragEvent): Boolean {
        if (event.action == DragEvent.ACTION_DROP) {

            isDropped = true
            var positionTarget: Int = -1

            val viewSource: View = event.localState as View
            val viewId: Int = view.id
            val dragIcon: Int = R.id.drag_icon

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

                val sourceAdapter = source.adapter as MainAdapter?
                val targetAdapter = target.adapter as MainAdapter?

                if (sourceAdapter != null && targetAdapter != null) {
                    val item: String = sourceAdapter.list[positionSource]
                    val sourceList: MutableList<String> = sourceAdapter.list
                    val targetList: MutableList<String> = targetAdapter.list

                    sourceList.removeAt(positionSource)
                    sourceAdapter.updateList(sourceList)
                    sourceAdapter.notifyDataSetChanged()

                    if (positionTarget >= 0) {
                        targetList.add(positionTarget, item)
                    } else {
                        targetList.add(item)
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

        return true
    }
}