package it.giovanni.kotlin.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AbsListView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MultiSwipeRefreshLayout(context: Context, attrs: AttributeSet?) : SwipeRefreshLayout(context, attrs) {

    private var mSwipeableChildren: Array<View?>? = null

    fun setSwipeableChildren(vararg ids: Int) {

        mSwipeableChildren = arrayOfNulls(ids.size)
        for (i in ids.indices) {
            mSwipeableChildren!![i] = findViewById(ids[i])
        }
    }

    override fun canChildScrollUp(): Boolean {
        if (mSwipeableChildren != null && mSwipeableChildren!!.isNotEmpty()) {
            // Iterate through the scrollable children and check if any of them can not scroll up.
            for (view in mSwipeableChildren!!) {
                if (view != null && view.isShown && !canViewScrollUp(view)) {
                    // If the view is shown, and can not scroll upwards, return false and start the gesture.
                    return false
                }
            }
        }
        return true
    }

    private fun canViewScrollUp(view: View): Boolean {
        return if (view is AbsListView) {
            view.childCount > 0 && (view.firstVisiblePosition > 0 || view.getChildAt(0).top < view.paddingTop)
        } else {
            view.scrollY > 0
        }
    }
}