package it.giovanni.arkivio.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import it.giovanni.arkivio.R
import it.giovanni.arkivio.utils.SharedPreferencesManager.loadDarkModeStateFromPreferences
import it.giovanni.arkivio.utils.Utils

class AvatarImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    private val drawPaint: Paint
    private var size: Float = 0F
    private var sWidth: Float = 0F
    private var center: Float = 0F

    init {
        sWidth = Utils.dpToPixel(2)
        drawPaint = Paint()
        drawPaint.strokeWidth = Utils.dpToPixel(2)
        drawPaint.style = Paint.Style.STROKE
        drawPaint.isAntiAlias = true
        drawPaint.alpha = 204

        val isDarkMode = loadDarkModeStateFromPreferences()
        if (isDarkMode)
            drawPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        else
            drawPaint.color = ContextCompat.getColor(context, R.color.azzurro)

        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                removeOnGlobalLayoutListener(this)
                center = (measuredWidth / 2.0F)
                size = center - sWidth / 2.0F
            }
        })
    }

    @Override
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(center, center, size, drawPaint)
    }

    private fun removeOnGlobalLayoutListener(listener: ViewTreeObserver.OnGlobalLayoutListener) {
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
}