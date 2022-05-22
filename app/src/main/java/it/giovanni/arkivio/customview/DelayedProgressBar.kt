package it.giovanni.arkivio.customview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.ProgressBar
import it.giovanni.arkivio.R

/**
 * Same as [androidx.core.widget.ContentLoadingProgressBar] where show delay and min show time can
 * be changed via style.
 */
class DelayedProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ProgressBar(context, attrs, defStyleAttr) {

    companion object {
        private val TAG = DelayedProgressBar::class.java.simpleName
        const val DEFAULT_SHOW_DELAY = 1000
        const val DEFAULT_MIN_SHOW_TIME = 500
    }

    private var minShowTime: Long = 0L // Once visible, the progressbar will be visible at least for a minimum time.
    private var minShowDelay: Long = 0L // Before shown, a minimum amout of time should pass.
    private var animationListener: AnimationListener? = null

    private var mStartTime: Long = -1
    private var mPostedHide = false
    private var mPostedShow = false
    private var mDismissed = false
    // val isDismissed: Boolean get() = mDismissed

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.DelayedProgressBar, defStyleAttr, 0)
        minShowDelay = array.getInt(R.styleable.DelayedProgressBar_delayStart, DEFAULT_SHOW_DELAY).toLong()
        minShowTime = array.getInt(R.styleable.DelayedProgressBar_minShowTime, DEFAULT_MIN_SHOW_TIME).toLong()
        Log.i(TAG, "showDelay: $minShowDelay, showTime: $minShowTime")

        array.recycle()
    }

    private val mDelayedHide = Runnable {
        Log.i(TAG, "mDelayedHide run")
        mPostedHide = false
        mStartTime = -1
        visibility = GONE
        Log.i(TAG, "visible = false")
        animationListener?.onAnimationEnd()
    }

    private val mDelayedShow = Runnable {
        Log.i(TAG, "mDelayedShow run")
        mPostedShow = false
        if (!mDismissed) {
            mStartTime = System.currentTimeMillis()
            visibility = VISIBLE
            Log.i(TAG, "visible = true")
            animationListener?.onAnimationStart()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        removeCallbacks()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks()
    }

    private fun removeCallbacks() {
        removeCallbacks(mDelayedHide)
        removeCallbacks(mDelayedShow)
    }

    /**
     * Hide the progress view if it is visible. The progress view will not be hidden until it has
     * been shown for at least a minimum show time. If the progress view was not yet visible,
     * cancels showing the progress view.
     */
    @Synchronized
    fun hide() {
        Log.i(TAG, "hide()")

        if (!isAttachedToWindow) {
            Log.i(TAG, "The view is not attached to a parent!")
            return
        }

        mDismissed = true
        removeCallbacks(mDelayedShow)
        mPostedShow = false
        val difference = System.currentTimeMillis() - mStartTime
        if (difference >= minShowTime || mStartTime == -1L) {
            // The progress spinner has been shown long enough, or was not shown yet.
            // If it wasn't shown yet, it will just never be shown.
            visibility = GONE
            Log.i(TAG, "visible = false")
            animationListener?.onAnimationEnd()
        } else {
            // The progress spinner is shown, but not long enough, so put a delayed message in to
            // hide it when its been shown long enough.
            if (!mPostedHide) {
                postDelayed(mDelayedHide, minShowTime - difference)
                mPostedHide = true
            }
        }
    }

    /**
     * Show the progress view after waiting for a minimum delay. If during that time, hide() is
     * called, the view is never made visible.
     */
    @Synchronized
    private fun internalShow(showDelay: Long) {
        Log.i(TAG, "internalShow(delay = $showDelay)")
        if (!isAttachedToWindow) {
            Log.i(TAG, "The view is not attached to a parent!")
            // printCallStack("DelayedProgressBar")
            return
        }

        // Reset the start time.
        mStartTime = -1
        mDismissed = false
        removeCallbacks(mDelayedHide)
        mPostedHide = false
        Log.i(TAG, "mPostedShow = $mPostedShow")
        if (!mPostedShow) {
            postDelayed(mDelayedShow, showDelay)
            mPostedShow = true
        }
    }

    fun show() {
        internalShow(minShowDelay)
    }

    fun showNow() {
        internalShow(0)
    }

    interface AnimationListener {
        fun onAnimationStart()
        fun onAnimationEnd()
    }
}