package it.giovanni.kotlin.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Base64
import androidx.core.content.ContextCompat
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Pattern
import kotlin.math.min

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
            val dimension = min(dimen, dimen)
            return ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension)
        }

        // Su W3B questo metodo viene utilizzato nella classe NotificationDetailFragment per trasformare in stringa un
        // testo Html.
        @Suppress("DEPRECATION")
        @SuppressLint("ObsoleteSdkInt")
        fun fromHtml(htmlMessage: String?): Spanned {
            var html = htmlMessage
            if (html == null)
                html = ""
            val result : Spanned
            result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(html)
            }
            return result
        }

        fun getVersionNameLong(versionName: String): Long {
            val versionList = versionName.split(".")
            var longVersion = ""
            for (item in versionList) {
                longVersion += item
            }
            return longVersion.toLong()
        }

        fun callContact(context: Context, phone: String) {
            try {
                val uri = "tel:$phone"
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(uri))
                context.startActivity(intent)
            } catch (e: Exception) {}
        }

        fun sendEmail(context: Context, email: String) {

            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "")
            intent.putExtra(Intent.EXTRA_TEXT, "")
            // intent.putExtra(Intent.EXTRA_HTML_TEXT, "") // If you are using HTML in your body text
            context.startActivity(Intent.createChooser(intent, ""))
        }

        fun checkEmail(email: String): Boolean {
            return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matcher(email).matches()
        }

        fun openBrowser(context: Context, link: String) {
            var url: String = link
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://$url"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            ContextCompat.startActivity(context, browserIntent, Bundle())
        }

        fun openApp(context: Context, packageName: String) {
            try {
                val manager = context.packageManager
                if (isPackageInstalled(packageName, manager)) {
                    val i = manager.getLaunchIntentForPackage(packageName)
                    i?.addCategory(Intent.CATEGORY_LAUNCHER)
                    ContextCompat.startActivity(context, i!!, Bundle())
                } else {
                    // app not installed, try to open google play store
                    val marketUri = Uri.parse("market://details?id=$packageName")
                    ContextCompat.startActivity(context, Intent(Intent.ACTION_VIEW).setData(marketUri), Bundle())
                }
            } catch (e: Exception) {
                // app not installed, google play store not installed, then open browser
                ContextCompat.startActivity(
                    context,
                    Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$packageName")),
                    Bundle()
                )
            }
        }

        private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
            return try {
                packageManager.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

        @Suppress("DEPRECATION")
        @SuppressLint("PackageManagerGetSignatures")
        fun getHashKey(context: Context): String? {

            var hashKey: String? = null
            try {
                val info : PackageInfo = context.packageManager!!.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                for (signature in info.signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    hashKey = String(Base64.encode(md.digest(), Base64.DEFAULT))
                }
            } catch (e: NoSuchAlgorithmException) {}
            catch (e: Exception) {}

            return hashKey
        }
    }
}