package it.giovanni.arkivio.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import it.giovanni.arkivio.R
import kotlin.math.min

class MultiSpinnerView : View {

    private var spinnerX: Float = 0.toFloat()
    private var spinnerY: Float = 0.toFloat()
    private var w: Float = 0.toFloat()
    private var h: Float = 0.toFloat()

    private var i0: Float = 0.toFloat()
    private var i1: Float = 0.toFloat()
    private var i2: Float = 0.toFloat()
    private var i3: Float = 0.toFloat()

    private var thickStrokeWidth: Float = 0.toFloat()
    private var thinStrokeWidth: Float = 0.toFloat()

    private var track: Paint? = null
    private var p1: Paint? = null
    private var p2: Paint? = null
    private var p3: Paint? = null
    private var p4: Paint? = null

    constructor(context: Context) : super(context) {
        setDefaults()
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setDefaults()
        applyAttributes(attrs)
    }
    constructor(context: Context, attrs: AttributeSet, theme: Int) : super(context, attrs, theme) {
        setDefaults()
        applyAttributes(attrs)
    }

    private fun setDefaults() {

        spinnerX = 0f
        spinnerY = 0f
        w = 0f
        h = 0f

        i0 = 0f
        i1 = 0f
        i2 = 0f
        i3 = 0f

        thickStrokeWidth = resources.displayMetrics.density * 8
        thinStrokeWidth = resources.displayMetrics.density * 1
    }

    private fun init() {

        val colorPrimary = Color.parseColor("#00B8DE")

        track = Paint(Paint.ANTI_ALIAS_FLAG)
        track!!.style = Paint.Style.STROKE
        track!!.color = -0x50506

        p1 = Paint(Paint.ANTI_ALIAS_FLAG)
        p2 = Paint(Paint.ANTI_ALIAS_FLAG)
        p3 = Paint(Paint.ANTI_ALIAS_FLAG)
        p4 = Paint(Paint.ANTI_ALIAS_FLAG)

        p1!!.style = Paint.Style.STROKE
        p2!!.style = Paint.Style.STROKE
        p3!!.style = Paint.Style.STROKE
        p4!!.style = Paint.Style.STROKE

        p1!!.color = -0xd7c3bb
        p2!!.color = colorPrimary
        p3!!.color = -0x615b58
        p4!!.color = -0x1d1c1c

        track!!.strokeWidth = thickStrokeWidth
        p1!!.strokeWidth = thinStrokeWidth
        p2!!.strokeWidth = thickStrokeWidth
        p3!!.strokeWidth = thickStrokeWidth
        p4!!.strokeWidth = thickStrokeWidth
    }

    private fun applyAttributes(attrs: AttributeSet) {

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.MultiSpinnerView, 0, 0)

        try {
            thickStrokeWidth = a.getDimension(R.styleable.MultiSpinnerView_thickStrokeWidth, thickStrokeWidth)
            thinStrokeWidth = a.getDimension(R.styleable.MultiSpinnerView_thinStrokeWidth, thinStrokeWidth)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            a.recycle()
        }

        init()
    }

    private fun postInvalidateCompat() {

        if (isAttachedToWindow)
            ViewCompat.postInvalidateOnAnimation(this)

        invalidate()
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        h = min(measuredWidth, measuredHeight).toFloat()
        w = h
        spinnerX = (if (w == measuredWidth.toFloat()) 0F else (measuredWidth - w) * .5f)
        spinnerY = (if (h == measuredHeight.toFloat()) 0F else (measuredHeight - h) * .5f)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postInvalidateCompat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(
            spinnerX + thickStrokeWidth * .5f,
            spinnerY + thickStrokeWidth * .5f,
            spinnerX + w - thickStrokeWidth * .5f,
            spinnerY + h - thickStrokeWidth * .5f,
            -90f,
            360f,
            false,
            track!!
        )
        canvas.drawArc(
            spinnerX + thickStrokeWidth * .5f,
            spinnerY + thickStrokeWidth * .5f,
            spinnerX + w - thickStrokeWidth * .5f,
            spinnerY + h - thickStrokeWidth * .5f,
            -90f,
            360.0f / i0 * i1,
            false,
            p4!!
        )
        canvas.drawArc(
            spinnerX + thickStrokeWidth * .5f,
            spinnerY + thickStrokeWidth * .5f,
            spinnerX + w - thickStrokeWidth * .5f,
            spinnerY + h - thickStrokeWidth * .5f,
            -90f,
            360.0f / i0 * (i2 + i3),
            false,
            p3!!
        )
        canvas.drawArc(
            spinnerX + thickStrokeWidth * .5f,
            spinnerY + thickStrokeWidth * .5f,
            spinnerX + w - thickStrokeWidth * .5f,
            spinnerY + h - thickStrokeWidth * .5f,
            -90f,
            360.0f / i0 * i2,
            false,
            p2!!
        )
        canvas.drawArc(
            spinnerX + thinStrokeWidth * .5f,
            spinnerY + thinStrokeWidth * .5f,
            spinnerX + w - thinStrokeWidth * .5f,
            spinnerY + h - thinStrokeWidth * .5f,
            -90f,
            360.0f / i0 * i1,
            false,
            p1!!
        )
    }

    /**
     * @param i0: è il valore totale
     * @param i1: disegna la sezione grigio chiaro e la linea nera
     * @param i2: disegna la sezione blu
     * @param i3: disegna la sezione grigio scuro
     */

    fun setValues(i0: Float, i1: Float, i2: Float, i3: Float) {
        this.i0 = i0
        this.i1 = i1
        this.i2 = i2
        this.i3 = i3
        postInvalidateCompat()
    }
}