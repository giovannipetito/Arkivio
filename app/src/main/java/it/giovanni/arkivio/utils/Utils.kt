@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Html
import android.text.Spanned
import android.util.Base64
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.squareup.picasso.Picasso
import it.giovanni.arkivio.App.Companion.context
import it.giovanni.arkivio.BuildConfig
import it.giovanni.arkivio.R
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.Executors
import java.util.regex.Pattern
import kotlin.math.min

class Utils {

    companion object {

        fun turnArrayListToString(list: ArrayList<String>): String {
            var string = ""
            for (i in list.indices) {
                string = if (i < list.size - 1)
                    string + list[i] + ", "
                else
                    string + list[i]
            }
            return string
        }

        fun turnArrayToString(array: Array<String>): String {
            var string = ""
            for (i in array.indices) {
                string = if (i < array.size - 1)
                    string + array[i] + " "
                else
                    string + array[i]
            }
            return string
        }

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

        @Suppress("DEPRECATION")
        fun setBitmapFromUrl(imageUrl: String, imageView: ImageView, activity: FragmentActivity) {
            try {
                Executors.newSingleThreadExecutor().execute {

                    val url: URL

                    if (imageUrl.startsWith("http:", ignoreCase = true) || imageUrl.startsWith("https:", ignoreCase = true)) {
                        url = URL(imageUrl)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.doInput = true
                        connection.connect()
                        try {
                            val input = connection.inputStream
                            val bitmap = BitmapFactory.decodeStream(input)

                            activity.runOnUiThread {

                                Picasso.get()
                                    .load(imageUrl)
                                    .into(imageView)

                                /*
                                Glide.with(context)
                                    .load(url)
                                    .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
                                    .into(imageView)
                                */

                                imageView.setImageBitmap(bitmap)
                            }
                        } catch (ex: FileNotFoundException) {
                            val defaultImage: ImageView = activity.findViewById(R.id.logo_app)
                            val bitmap: Bitmap = (defaultImage.drawable as BitmapDrawable).bitmap
                            activity.runOnUiThread {
                                imageView.setImageBitmap(bitmap)
                                imageView.setColorFilter(ContextCompat.getColor(context, R.color.dark))
                            }
                        }
                    } else {
                        val defaultImage: ImageView = activity.findViewById(R.id.logo_app)
                        val bitmap: Bitmap = (defaultImage.drawable as BitmapDrawable).bitmap
                        activity.runOnUiThread {
                            imageView.setImageBitmap(bitmap)
                            imageView.setColorFilter(context.resources.getColor(R.color.dark))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun getBitmapFromAsset(context: Context, filePath: String?): Bitmap? {
            val assetManager = context.assets
            val inputStream: InputStream
            var bitmap: Bitmap? = null
            try {
                inputStream = assetManager.open(filePath!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bitmap
        }

        // Su W3B questo metodo viene utilizzato nella classe NotificationDetailFragment per
        // trasformare in stringa un testo Html.
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

        private fun sendEmail(context: Context) {
            val to = arrayOf("")
            val cc = arrayOf("")
            val intent = Intent(Intent.ACTION_SEND)
            intent.data = Uri.parse("mailto:")
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, to)
            intent.putExtra(Intent.EXTRA_CC, cc)
            intent.putExtra(Intent.EXTRA_SUBJECT, "") // Email subject.
            intent.putExtra(Intent.EXTRA_TEXT, "") // Email message.
            try {
                context.startActivity(Intent.createChooser(intent, "Send mail..."))
                // currentActivity.finish()
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_LONG).show()
            }
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

        fun encodeBase64Url(decodedUrl: String): String {
            var encodedUrl = ""
            try {
                val baos = ByteArrayOutputStream()
                val oos: ObjectOutputStream
                oos = ObjectOutputStream(baos)
                oos.writeObject(decodedUrl)
                oos.close()
                encodedUrl = Base64.encodeToString(baos.toByteArray(), Base64.URL_SAFE)
                baos.close()
            } catch (e: Exception) {
            }
            return encodedUrl
        }

        fun decodeBase64Url(encodedUrl: String): String {
            var decodedUrl = ""
            try {
                val data: ByteArray? = Base64.decode(encodedUrl, Base64.URL_SAFE)
                val ois: ObjectInputStream
                ois = ObjectInputStream(ByteArrayInputStream(data))
                decodedUrl = ois.readObject() as String
                ois.close()
            } catch (e: Exception) {
            }
            return decodedUrl
        }

        fun getFileUri(file: File, context: Context): Uri {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
        }

        /*
        fun isAutologinEnabled(): Boolean {
            return if (PermissionManager.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE))
                isOnline() && isOnMobileConnection() && isSimKena()
            else false
        }
        */

        @Suppress("DEPRECATION")
        fun isOnline(): Boolean {
            var status = false
            val manager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = manager.activeNetworkInfo
            if (networkInfo != null) {
                val state: NetworkInfo.State = networkInfo.state
                status = NetworkInfo.State.CONNECTED == state
            }
            return status
        }

        /**
         * Check if the device is connected (or connecting) to a WiFi network.
         * @return true if connected or connecting, false otherwise.
         */
        @Suppress("DEPRECATION")
        fun isOnWiFiConnection(): Boolean {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }

        @Suppress("DEPRECATION")
        fun isOnMobileConnection(): Boolean {
            var result = false
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (manager.activeNetworkInfo != null) {
                try {
                    val networkInfo = manager.activeNetworkInfo
                    if (networkInfo != null) {
                        val networkType = networkInfo.type
                        result = networkType == ConnectivityManager.TYPE_MOBILE
                    }
                } catch (e: java.lang.Exception) {
                }
            }
            return result
        }

        fun getDeviceLanguage(): String? {
            return Locale.getDefault().toString()
        }

        fun getDeviceSoftwareVersion(): String {
            return if (PermissionManager.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                // Permission Granted
                val manager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val deviceSoftwareVersion = manager.deviceSoftwareVersion

                deviceSoftwareVersion
            } else {
                "Permission required"
            }
        }

        fun getSimOperator(): String {
            return if (PermissionManager.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                // Permission Granted
                val manager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val simOperator: String = manager.simOperator

                simOperator
            } else {
                "Permission denied"
            }
        }

        fun getSimOperatorName(): String {
            return if (PermissionManager.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                // Permission Granted
                val manager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val simOperatorName = manager.simOperatorName

                simOperatorName
            } else {
                "Permission denied"
            }
        }

        fun isSimKena(): Boolean {
            return if (PermissionManager.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                // Permission Granted
                val manager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val simOperator: String = manager.simOperator

                simOperator.startsWith("22207")
            } else {
                // Permission denied
                false
            }
        }

        @SuppressLint("HardwareIds")
        fun getLine1Number(): String {
            return if (PermissionManager.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                val manager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val line1Number = manager.line1Number

                line1Number
            } else {
                "Permission denied"
            }
        }

        @SuppressLint("HardwareIds")
        fun getSimSerialNumber(): String {
            return if (PermissionManager.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                val manager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val simSerialNumber = manager.simSerialNumber

                simSerialNumber
            } else {
                "Permission denied"
            }
        }

        fun getSimCountryIso(): String {
            return if (PermissionManager.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                val manager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val simCountryIso = manager.simCountryIso

                simCountryIso
            } else {
                "Permission denied"
            }
        }

        fun getNetworkOperator(): String {
            return if (PermissionManager.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                val manager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val networkOperator = manager.networkOperator

                networkOperator
            } else {
                "Permission denied"
            }
        }

        fun getNetworkOperatorName(): String {
            return if (PermissionManager.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                val manager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val networkOperatorName = manager.networkOperatorName

                networkOperatorName
            } else {
                "Permission denied"
            }
        }

        fun getNetworkCountryIso(): String {
            return if (PermissionManager.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                val manager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val networkCountryIso = manager.networkCountryIso

                networkCountryIso
            } else {
                "Permission denied"
            }
        }

        @Suppress("DEPRECATION")
        fun setTextWebview(webview: WebView, body: String, context: Context) {

            val head = "<head><style>@font-face {font-family: 'Fira';src: url('file:///android_asset/fonts/fira_medium.ttf');}body {font-family: 'Fira';}</style></head>"
            val text = "<font color='#678AC5'>" +
                    "<html>$head<body style=\"font-family: Fira\">$body</body></html>"
            webview.settings.textSize = WebSettings.TextSize.NORMAL

            /*
            val fontSize = context.resources.getDimension(R.dimen.dimen_16dp)
            webview.settings.defaultFontSize = fontSize.toInt()

            webview.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            webview.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            webview.settings.loadsImagesAutomatically = true
            webview.settings.setGeolocationEnabled(false)
            webview.settings.setNeedInitialFocus(false)
            webview.settings.setAppCacheEnabled(false)
            webview.settings.blockNetworkImage = true
            webview.settings.saveFormData = false
            */

            /*
            if (Build.MODEL.startsWith("FIG-LX1")) {
                webview.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(wView: WebView?, url: String?): Boolean {
                        val catchedUri = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        catchedUri.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(catchedUri)
                        return true
                    }
                }
            }
            */
            webview.setPadding(0, 0, 0, 0)
            webview.setBackgroundColor(context.resources.getColor(R.color.colorAccent))
            webview.loadDataWithBaseURL("", text, "text/html", "utf-8", "")
        }
    }
}