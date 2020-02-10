package it.giovanni.kotlin.youtube.videowall

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView

/**
 * A view which flips from one ImageView to another view using a 3D flip animation.
 */
class FlippingView(context: Context?, private val listener: Listener, width: Int, height: Int) : FrameLayout(context!!) {

    private val flipOutView: ImageView = ImageView(context)
    private val flipInView: ImageView = ImageView(context)
    private val animations: AnimatorSet

    /**
     * Defines an interface to enable listening to flip events.
     */
    interface Listener {
        /**
         * Called when the FlippingView has completed a flip.
         * @param view The FlippingView which has completed the flip.
         */
        fun onFlipped(view: FlippingView)
    }

    fun setFlipInDrawable(drawable: Drawable?) {
        flipInView.setImageDrawable(drawable)
    }

    fun setFlipOutDrawable(drawable: Drawable?) {
        flipOutView.setImageDrawable(drawable)
    }

    fun setFlipDuration(flipDuration: Int) {
        animations.duration = flipDuration.toLong()
    }

    fun flip() {
        animations.start()
    }

    /**
     * Listens to the end of the flip animation to signal to listeners that the flip is complete
     */
    inner class AnimationListener : AnimatorListenerAdapter() {

        override fun onAnimationEnd(animation: Animator) {
            flipOutView.rotationY = 0f
            flipInView.rotationY = -90f
            listener.onFlipped(this@FlippingView)
        }
    }

    init {
        addView(flipOutView, width, height)
        addView(flipInView, width, height)
        flipInView.rotationY = -90f
        val flipOutAnimator = ObjectAnimator.ofFloat(flipOutView, "rotationY", 0f, 90f)
        flipOutAnimator.interpolator = AccelerateInterpolator()
        val flipInAnimator: Animator = ObjectAnimator.ofFloat(flipInView, "rotationY", -90f, 0f)
        flipInAnimator.interpolator = DecelerateInterpolator()
        animations = AnimatorSet()
        animations.playSequentially(flipOutAnimator, flipInAnimator)
        animations.addListener(AnimationListener())
    }
}