package it.giovanni.arkivio.fragments.detail.drag

import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.model.favorite.Favorite
import java.util.Collections

/*
Implements DragListener with the following rules:
- The items of favoritesRecyclerview list can be swiped between them, the items of availablesRecyclerview list not.
- If you drag the item of favoritesRecyclerview list to availablesRecyclerview list, delete the item from favoritesRecyclerview list.
- If you drag the item of availablesRecyclerview list to favoritesRecyclerview list, delete the item from availablesRecyclerview list and add it to favoritesRecyclerview list.

- If you drag the item of availablesRecyclerview list to favoritesRecyclerview list, the next items should shift to the end and if the favoritesRecyclerview list has size 7, delete the last item and add the deleted item to availablesRecyclerview list.

Update the adapter everytime the rules are applied.
 */
class DragListener(private val listener: Listener?) : OnDragListener {

    override fun onDrag(view: View, event: DragEvent): Boolean {
        if (event.action == DragEvent.ACTION_DROP) {
            val viewSource: View = event.localState as View
            val viewId: Int = view.id

            val favoritesContainer: Int = R.id.favorites_container
            val availablesContainer: Int = R.id.availables_container

            val favoritesRecyclerview: Int = R.id.favorites_recyclerview
            val availablesRecyclerview: Int = R.id.availables_recyclerview

            val target: RecyclerView = when (viewId) {
                favoritesContainer, favoritesRecyclerview -> {
                    view.rootView.findViewById(favoritesRecyclerview)
                }

                availablesContainer, availablesRecyclerview -> {
                    view.rootView.findViewById(availablesRecyclerview)
                }

                else -> {
                    view.parent as RecyclerView
                }
            }

            val source = viewSource.parent as RecyclerView
            val sourcePosition = viewSource.tag as Int
            val sourceAdapter = source.adapter as DragAdapter?
            val targetAdapter = target.adapter as DragAdapter?

            if (sourceAdapter != null && targetAdapter != null) {
                val favorite: Favorite = sourceAdapter.currentList[sourcePosition]
                val sourceList: MutableList<Favorite> = sourceAdapter.currentList.toMutableList()
                val targetList: MutableList<Favorite> = targetAdapter.currentList.toMutableList()

                Log.i("[DRAG]", "1) sourcePosition: $sourcePosition")
                Log.i("[DRAG]", "1) sourceList: " + sourceList.toList())
                Log.i("[DRAG]", "1) targetList: " + targetList.toList())

                // Rule 1: Reordering within the same favoritesRecyclerview
                if (source.id == favoritesRecyclerview && target.id == favoritesRecyclerview) {
                    // Swap items at positionSource and target position
                    val targetPosition = target.getChildAdapterPosition(view)

                    if (targetPosition != RecyclerView.NO_POSITION && sourcePosition != targetPosition) {
                        Collections.swap(sourceList, sourcePosition, targetPosition)

                        // Update the list in the adapter
                        val newSourceList = sourceList.toList() // Create a new list instance
                        sourceAdapter.submitList(newSourceList)
                        // sourceAdapter.submitList(sourceList)
                        // targetAdapter.submitList(sourceList) // Same list for both since it's a reordering

                        Log.i("[DRAG]", "2) sourcePosition: $sourcePosition, targetPosition: $targetPosition")
                        Log.i("[DRAG]", "2) sourceList: " + sourceList.toList())
                        Log.i("[DRAG]", "2) targetList: " + targetList.toList())
                    }
                }

                // Rule 2: Moving from availables to favorites
                if (source.id == availablesRecyclerview && target.id == favoritesRecyclerview) {
                    // Remove from availables, add to favorites
                    sourceList.removeAt(sourcePosition)
                    targetList.add(favorite)
                    sourceAdapter.submitList(sourceList)
                    targetAdapter.submitList(targetList)

                    Log.i("[DRAG]", "Rule 2) sourceList.size: ${sourceList.size}, targetList.size: ${targetList.size}")
                }

                // Rule 3: Moving from favorites to availables
                if (source.id == favoritesRecyclerview && target.id == availablesRecyclerview) {
                    // Remove from favorites, add to availables
                    sourceList.removeAt(sourcePosition)
                    targetList.add(favorite)
                    sourceAdapter.submitList(sourceList)
                    targetAdapter.submitList(targetList)

                    Log.i("[DRAG]", "Rule 3) sourceList.size: ${sourceList.size}, targetList.size: ${targetList.size}")
                }

                // Notify the listener when the list is empty
                if (sourceList.isEmpty()) {
                    if (source.id == availablesRecyclerview) {
                        listener?.notifyEmptyAvailables()
                    } else if (source.id == favoritesRecyclerview) {
                        listener?.notifyEmptyFavorites()
                    }
                }
            }
        }
        return true
    }
}