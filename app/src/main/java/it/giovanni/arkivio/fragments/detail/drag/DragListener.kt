package it.giovanni.arkivio.fragments.detail.drag

import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.detail.dragdrop.Favorite

class DragListener(private val listener: Listener?) : OnDragListener {

    private var isDropped: Boolean = false

    override fun onDrag(view: View, event: DragEvent): Boolean {

        if (event.action == DragEvent.ACTION_DROP) {

            isDropped = true
            var positionTarget: Int = -1

            val viewSource: View = event.localState as View
            val viewId: Int = view.id
            val dragIcon: Int = R.id.item

            val topRecyclerviewContainer: Int = R.id.top_recyclerview_container
            val bottomRecyclerviewContainer: Int = R.id.bottom_recyclerview_container

            val topRecyclerview: Int = R.id.top_recyclerview
            val bottomRecyclerview: Int = R.id.bottom_recyclerview

            val target: RecyclerView

            if (viewId == dragIcon || viewId == topRecyclerviewContainer || viewId == bottomRecyclerviewContainer || viewId == topRecyclerview || viewId == bottomRecyclerview) {
                when (viewId) {
                    topRecyclerviewContainer, topRecyclerview -> {
                        target = view.rootView.findViewById(topRecyclerview)
                    }
                    bottomRecyclerviewContainer, bottomRecyclerview -> {
                        target = view.rootView.findViewById(bottomRecyclerview)
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

                    // Check if the item is being moved within the same list
                    if (sourceId == target.id) {
                        // Same list, no need to update the isPersonal flag
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
                    } else {
                        // Item is being moved to a different list, update the isPersonal flag
                        if (sourceId == topRecyclerview) {
                            favorite.isPersonal = false // Item moving to bottom list
                        } else if (sourceId == bottomRecyclerview) {
                            favorite.isPersonal = true // Item moving to top list
                        }

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
                    }

                    // Notify listener when list is empty
                    if ((sourceId == bottomRecyclerview && sourceAdapter.itemCount < 1) || viewId == bottomRecyclerviewContainer) {
                        listener?.notifyAvailableFavoritesEmpty()
                    }

                    if ((sourceId == topRecyclerview && sourceAdapter.itemCount < 1) || viewId == topRecyclerviewContainer) {
                        listener?.notifyPersonalFavoritesEmpty()
                    }
                }
            }
        }

        return true
    }
}