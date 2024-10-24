package it.giovanni.arkivio.fragments.detail.drag

import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.model.favorite.Favorite

class DragListener(private val listener: Listener?) : OnDragListener {

    override fun onDrag(view: View, event: DragEvent): Boolean {

        if (event.action == DragEvent.ACTION_DROP) {

            var positionTarget: Int = -1

            val viewSource: View = event.localState as View
            val viewId: Int = view.id
            val dragIcon: Int = R.id.item

            val favoritesContainer: Int = R.id.favorites_container
            val availablesContainer: Int = R.id.availables_container

            val favoritesRecyclerview: Int = R.id.favorites_recyclerview
            val availablesRecyclerview: Int = R.id.availables_recyclerview

            val target: RecyclerView

            if (viewId == dragIcon || viewId == favoritesContainer || viewId == availablesContainer || viewId == favoritesRecyclerview || viewId == availablesRecyclerview) {
                when (viewId) {
                    favoritesContainer, favoritesRecyclerview -> {
                        target = view.rootView.findViewById(favoritesRecyclerview)
                    }
                    availablesContainer, availablesRecyclerview -> {
                        target = view.rootView.findViewById(availablesRecyclerview)
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
                    val favorite: Favorite = sourceAdapter.favorites[positionSource]
                    val sourceList: MutableList<Favorite> = sourceAdapter.favorites
                    val targetList: MutableList<Favorite> = targetAdapter.favorites

                    // Notify the listener when the list is empty.
                    if ((sourceId == availablesRecyclerview && sourceAdapter.itemCount < 1) || viewId == availablesContainer) {
                        listener?.notifyEmptyAvailables()
                    }

                    if ((sourceId == favoritesRecyclerview && sourceAdapter.itemCount < 1) || viewId == favoritesContainer) {
                        listener?.notifyEmptyFavorites()
                    }
                }
            }
        }

        return true
    }
}