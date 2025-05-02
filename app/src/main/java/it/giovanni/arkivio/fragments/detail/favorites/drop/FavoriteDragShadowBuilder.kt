package it.giovanni.arkivio.fragments.detail.favorites.drop

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View

class FavoriteDragShadowBuilder(view: View, private val badge: Drawable?, private val showBadge: Boolean) : View.DragShadowBuilder(view) {

    override fun onDrawShadow(canvas: Canvas) {
        view.draw(canvas)

        if (showBadge) {
            val iconSize = 40
            val left = 10
            val top = 10
            badge?.setBounds(left, top, left + iconSize, top + iconSize)
            badge?.draw(canvas)
        }
    }
}