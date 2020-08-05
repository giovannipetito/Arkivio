package it.giovanni.arkivio.fragments.detail.youtube.videowall

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Pair
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import java.util.*

/**
 * A view which displays a grid of images.
 */
class ImageWallView(context: Context,
    private val imageWidth: Int,
    private val imageHeight: Int,
    private val interImagePadding: Int) : ViewGroup(context) {

    private val random: Random = Random()
    private var images: Array<ImageView?>
    private val unInitializedImages: MutableList<Int>
    private var numberOfColumns = 0
    private var numberOfRows = 0

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        // create enough columns to fill view's width, plus an extra column at either
        // side to allow images to have diagonal offset across the screen.
        numberOfColumns = width / (imageWidth + interImagePadding) + 2
        // create enough rows to fill the view's height (adding an extra row at bottom if necessary).
        numberOfRows = height / (imageHeight + interImagePadding)
        numberOfRows += if (height % (imageHeight + interImagePadding) == 0) 0 else 1
        check(!(numberOfRows <= 0 || numberOfColumns <= 0)) {
            ("Error creating an ImageWallView with " + numberOfRows
                    + " rows and " + numberOfColumns + " columns. Both values must be greater than zero.")
        }
        if (images.size < numberOfColumns * numberOfRows) {
            images = images.copyOf(numberOfColumns * numberOfRows)
        }
        removeAllViews()
        for (col in 0 until numberOfColumns) {
            for (row in 0 until numberOfRows) {
                val elementIdx = getElementIdx(col, row)
                if (images[elementIdx] == null) {
                    val thumbnail = ImageView(context)
                    thumbnail.layoutParams = LayoutParams(imageWidth, imageHeight)
                    images[elementIdx] = thumbnail
                    unInitializedImages.add(elementIdx)
                }
                addView(images[elementIdx])
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val displayMetrics = resources.displayMetrics
        val width = View.getDefaultSize(displayMetrics.widthPixels, widthMeasureSpec)
        val height = View.getDefaultSize(displayMetrics.heightPixels, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (col in 0 until numberOfColumns) {
            for (row in 0 until numberOfRows) {
                val x = (col - 1) * (imageWidth + interImagePadding) + row * (imageWidth / numberOfRows)
                val y = row * (imageHeight + interImagePadding)
                images[col * numberOfRows + row]!!.layout(x, y, x + imageWidth, y + imageHeight)
            }
        }
    }

    fun getXPosition(col: Int, row: Int): Int {
        return images[getElementIdx(col, row)]!!.left
    }

    fun getYPosition(col: Int, row: Int): Int {
        return images[getElementIdx(col, row)]!!.top
    }

    private fun getElementIdx(col: Int, row: Int): Int {
        return col * numberOfRows + row
    }

    fun hideImage(col: Int, row: Int) {
        images[getElementIdx(col, row)]!!.visibility = View.INVISIBLE
    }

    fun showImage(col: Int, row: Int) {
        images[getElementIdx(col, row)]!!.visibility = View.VISIBLE
    }

    fun setImageDrawable(col: Int, row: Int, drawable: Drawable?) {
        val elementIdx = getElementIdx(col, row)
        // manually boxing elementIdx to avoid calling List.remove(int position) method overload
        unInitializedImages.remove(Integer.valueOf(elementIdx))
        images[elementIdx]!!.setImageDrawable(drawable)
    }

    fun getImageDrawable(col: Int?, row: Int?): Drawable? {
        val elementIdx = getElementIdx(col!!, row!!)
        return images[elementIdx]!!.drawable
    }

    // Don't choose the first or last columns (since they are partly hidden)
    val nextLoadTarget: Pair<Int, Int>
        get() {
            var nextElement: Int
            do {
                nextElement =
                    if (unInitializedImages.isEmpty()) { // Don't choose the first or last columns (since they are partly hidden)
                        random.nextInt((numberOfColumns - 2) * numberOfRows) + numberOfRows
                    } else {
                        unInitializedImages[random.nextInt(unInitializedImages.size)]
                    }
            } while (images[nextElement]!!.visibility != View.VISIBLE)
            val col = nextElement / numberOfRows
            val row = nextElement % numberOfRows
            return Pair(col, row)
        }

    fun allImagesLoaded(): Boolean {
        return unInitializedImages.isEmpty()
    }

    init {
        images = arrayOfNulls(0)
        unInitializedImages = ArrayList()
    }
}