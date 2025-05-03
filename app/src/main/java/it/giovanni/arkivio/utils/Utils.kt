package it.giovanni.arkivio.utils

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.BatteryManager
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
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.squareup.picasso.Picasso
import it.giovanni.arkivio.App.Companion.context
import it.giovanni.arkivio.BuildConfig
import it.giovanni.arkivio.R
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.math.min
import androidx.core.graphics.createBitmap

object Utils {

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
        val output = createBitmap(dimen, dimen)
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
        return if (bitmap.width != bitmap.height) {
            val dimen: Int = min(bitmap.width, bitmap.height)
            ThumbnailUtils.extractThumbnail(bitmap, dimen, dimen)
        } else bitmap
    }

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

                            imageView.setImageBitmap(bitmap)
                        }
                    } catch (ex: FileNotFoundException) {
                        println(ex.message)
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
                        imageView.setColorFilter(ContextCompat.getColor(context, R.color.dark))
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

    fun fromHtml(htmlMessage: String?): Spanned {
        var html = htmlMessage
        if (html == null)
            html = ""
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    }

    fun getVersionNameLong(versionName: String): Long {
        val versionList = versionName.split(".")
        var longVersion = ""
        for (item in versionList) {
            longVersion += item
        }
        return longVersion.toLong()
    }

    fun callContact1(context: Context, phone: String) {
        try {
            val uri = "tel:$phone"
            val intent = Intent(Intent.ACTION_DIAL, uri.toUri())
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun callContact2(context: Context, label: String) {

        val phone = label.substring(label.indexOf(": "), label.length)
        try {
            val uri = "tel:$phone"
            val intent = Intent(Intent.ACTION_DIAL, uri.toUri())
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendSimpleMail(context: Context, email: String) {
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:$email".toUri())
        try {
            context.startActivity(Intent.createChooser(intent, "Send mail..."))
        } catch (ex: ActivityNotFoundException) {
            println(ex.message)
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendFilledOutMail(context: Context, to: Array<String>, cc: Array<String>, subject: String, text: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:".toUri()
        intent.putExtra(Intent.EXTRA_EMAIL, to)
        intent.putExtra(Intent.EXTRA_CC, cc)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject) // Email subject.
        intent.putExtra(Intent.EXTRA_TEXT, text) // Email text.
        try {
            context.startActivity(Intent.createChooser(intent, "Send mail..."))
            // currentActivity.finish()
        } catch (ex: ActivityNotFoundException) {
            println(ex.message)
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendGmailMail(context: Context, to: Array<String>, cc: Array<String>, subject: String, text: String) {
        val gmailIntent = Intent(Intent.ACTION_SEND)
            .setType("plain/text") // .setType("text/plain")
            .setPackage("com.google.android.gm")
            .putExtra(Intent.EXTRA_EMAIL, to)
            .putExtra(Intent.EXTRA_CC, cc)
            .putExtra(Intent.EXTRA_SUBJECT, subject)
            .putExtra(Intent.EXTRA_TEXT, text)
        try {
            context.startActivity(gmailIntent)
        } catch (ex: ActivityNotFoundException) {
            println(ex.message)
            // There is no Gmail client installed.
            Toast.makeText(context, "There is no Gmail client installed.", Toast.LENGTH_SHORT).show()
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = "mailto:".toUri()
            intent.putExtra(Intent.EXTRA_EMAIL, to)
            intent.putExtra(Intent.EXTRA_CC, cc)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            try {
                context.startActivity(Intent.createChooser(intent, "Send mail..."))
            } catch (ex: ActivityNotFoundException) {
                println(ex.message)
                Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendOutlookMail(context: Context, to: Array<String>, cc: Array<String>, subject: String, text: String) {
        val outlookIntent = Intent(Intent.ACTION_SEND)
            .setType("plain/text") // .setType("text/plain")
            .setPackage("com.microsoft.office.outlook")
            .putExtra(Intent.EXTRA_EMAIL, to)
            .putExtra(Intent.EXTRA_CC, cc)
            .putExtra(Intent.EXTRA_SUBJECT, subject)
            .putExtra(Intent.EXTRA_TEXT, text)
        try {
            context.startActivity(outlookIntent)
        } catch (ex: ActivityNotFoundException) {
            println(ex.message)
            // There is no Outlook client installed.
            Toast.makeText(context, "There is no Outlook client installed.", Toast.LENGTH_SHORT).show()
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = "mailto:".toUri()
            intent.putExtra(Intent.EXTRA_EMAIL, to)
            intent.putExtra(Intent.EXTRA_CC, cc)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            try {
                context.startActivity(Intent.createChooser(intent, "Send mail..."))
            } catch (ex: ActivityNotFoundException) {
                println(ex.message)
                Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkEmail(email: String): Boolean {
        return Patterns.emailPattern.matcher(email).matches()
    }

    fun openBrowser(context: Context, link: String) {
        var url: String = link
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://$url"
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
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
                val marketUri = "market://details?id=$packageName".toUri()
                ContextCompat.startActivity(context, Intent(Intent.ACTION_VIEW).setData(marketUri), Bundle())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // app not installed, google play store not installed, then open browser
            ContextCompat.startActivity(
                context,
                Intent(Intent.ACTION_VIEW,
                    "http://play.google.com/store/apps/details?id=$packageName".toUri()),
                Bundle()
            )
        }
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    fun getHashKey(context: Context): String? {

        var hashKey: String? = null
        try {
            val info : PackageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                hashKey = String(Base64.encode(md.digest(), Base64.DEFAULT))
            }
        }
        catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        return hashKey
    }

    fun encodeBase64Url(decodedUrl: String): String {
        var encodedUrl = ""
        try {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(decodedUrl)
            oos.close()
            encodedUrl = Base64.encodeToString(baos.toByteArray(), Base64.URL_SAFE)
            baos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return encodedUrl
    }

    fun decodeBase64Url(encodedUrl: String): String {
        var decodedUrl = ""
        try {
            val data: ByteArray? = Base64.decode(encodedUrl, Base64.URL_SAFE)
            val ois = ObjectInputStream(ByteArrayInputStream(data))
            decodedUrl = ois.readObject() as String
            ois.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return decodedUrl
    }

    fun getFileUri(file: File, context: Context): Uri {
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
    }

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
    fun isOnWiFiConnection(): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

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
                e.printStackTrace()
            }
        }
        return result
    }

    fun getDeviceLanguage(): String {
        return Locale.getDefault().toString()
    }

    fun getDeviceSoftwareVersion(): String? {
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

    fun getLine1Number(): String {
        return if (PermissionManager.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            val manager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val line1Number = manager.line1Number
            line1Number
        } else {
            "Permission denied"
        }
    }

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

    fun setTextWebview(webview: WebView, body: String, context: Context) {

        val head = "<head><style>@font-face {font-family: 'Fira';src: url('file:///android_asset/fonts/fira_medium.ttf');}body {font-family: 'Fira';}</style></head>"
        val text = "<font color='#678AC5'>" +
                "<html>$head<body style=\"font-family: Fira\">$body</body></html>"
        webview.settings.textSize = WebSettings.TextSize.NORMAL

        webview.setPadding(0, 0, 0, 0)
        webview.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
        webview.loadDataWithBaseURL("", text, "text/html", "utf-8", "")
    }

    fun getJsonFromAssets(context: Context, fileName: String): String? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, charset("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun getBatteryCapacity(context: Context): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    fun clearCache(context: Context?) {
        context?.dataDir?.deleteRecursively()
        context?.dataDir?.delete()
        context?.cacheDir?.deleteRecursively()
        context?.cacheDir?.delete()
    }
}