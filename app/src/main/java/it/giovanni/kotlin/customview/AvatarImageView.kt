package it.giovanni.kotlin.customview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.content.ContextCompat
import it.giovanni.kotlin.utils.Utils
import it.giovanni.kotlin.R

class AvatarImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {
    private val drawPaint: Paint
    private var size: Float = 0.toFloat()
    private var sWidth: Float = 0.toFloat()
    private var center: Float = 0.toFloat()

    init {
        sWidth = Utils.dpToPixel(2)
        drawPaint = Paint()
        drawPaint.color = ContextCompat.getColor(context, R.color.white)
        drawPaint.strokeWidth = Utils.dpToPixel(2)
        drawPaint.style = Paint.Style.STROKE
        drawPaint.isAntiAlias = true
        drawPaint.alpha = 204

        val vto = viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                removeOnGlobalLayoutListener(this)
                center = (measuredWidth / 2.0F)
                size = center - sWidth / 2.0F
            }
        })
    }

    @Override
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(center, center, size, drawPaint)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun removeOnGlobalLayoutListener(listener: ViewTreeObserver.OnGlobalLayoutListener) {
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
}