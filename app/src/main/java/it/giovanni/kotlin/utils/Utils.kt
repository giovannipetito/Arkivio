package it.giovanni.kotlin.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.media.ThumbnailUtils
import android.os.Build
import android.text.Html
import android.text.Spanned

class Utils {

    companion object {

        private const val S6_EDGE_PLUS = "SM-G928F"
        fun convertDpToPixel(context: Context, dp: Float): Int {
            return if (Build.MODEL.startsWith(S6_EDGE_PLUS))
                (dp * (context.resources.displayMetrics.density + 0.5f)).toInt()
            else
                (dp * context.resources.displayMetrics.density + 0.5f).toInt()
        }

        fun dpToPixel(dp: Int): Float {
            val metrics = Resources.getSystem().displayMetrics
            return dp * (metrics.densityDpi / 160f) // px
        }

        fun getRoundBitmap(bitmap : Bitmap, roundPixelSize : Int) : Bitmap {
            var dimen : Int = bitmap.width
            if (bitmap.width > bitmap.height)
                dimen = bitmap.height
            val output = Bitmap.createBitmap(dimen, dimen, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val paint = Paint()
            val rect = Rect(0, 0, dimen, dimen)
            val rectF = RectF(rect)
            val roundPx = roundPixelSize.toFloat()
            paint.isAntiAlias = true
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(cropCenter(bitmap), rect, rect, paint)
            return output
        }

        private fun cropCenter(bitmap: Bitmap): Bitmap {
            var dimen: Int = bitmap.width
            if (bitmap.width > bitmap.height)
                dimen = bitmap.height
            val dimension = Math.min(dimen, dimen)
            return ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension)
        }

        @SuppressLint("ObsoleteSdkInt")
        fun fromHtml(htmlMessage: String?): Spanned {
            var html = htmlMessage
            if (html == null)
                html = ""
            val result : Spanned
            result = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(html)
            }
            return result
        }
    }
}