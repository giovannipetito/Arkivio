package it.giovanni.arkivio.fragments.detail.permissions

import android.Manifest
import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.popup.CustomDialogPopup
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.PermissionManager
import it.giovanni.arkivio.utils.Utils
import it.giovanni.arkivio.utils.Utils.Companion.decodeBase64Url
import it.giovanni.arkivio.utils.Utils.Companion.encodeBase64Url
import it.giovanni.arkivio.utils.Utils.Companion.getDeviceLanguage
import it.giovanni.arkivio.utils.Utils.Companion.getDeviceSoftwareVersion
import it.giovanni.arkivio.utils.Utils.Companion.getLine1Number
import it.giovanni.arkivio.utils.Utils.Companion.getNetworkCountryIso
import it.giovanni.arkivio.utils.Utils.Companion.getNetworkOperator
import it.giovanni.arkivio.utils.Utils.Companion.getNetworkOperatorName
import it.giovanni.arkivio.utils.Utils.Companion.getSimCountryIso
import it.giovanni.arkivio.utils.Utils.Companion.getSimOperator
import it.giovanni.arkivio.utils.Utils.Companion.getSimOperatorName
import it.giovanni.arkivio.utils.Utils.Companion.getSimSerialNumber
import it.giovanni.arkivio.utils.Utils.Companion.isOnMobileConnection
import it.giovanni.arkivio.utils.Utils.Companion.isOnWiFiConnection
import it.giovanni.arkivio.utils.Utils.Companion.isOnline
import it.giovanni.arkivio.utils.Utils.Companion.isSimKena
import kotlinx.android.synthetic.main.permissions_layout.*
import java.io.File
import java.util.*

class PermissionsFragment : DetailFragment(), PermissionManager.PermissionListener {

    private var viewFragment: View? = null
    private var isDownloading: Boolean = false
    private var hasPermission: Boolean = false
    private var hasPhonePermission: Boolean = false
    private lateinit var customPopup: CustomDialogPopup
    private lateinit var action: Action
    private lateinit var url: String
    private lateinit var fileName: String
    private lateinit var filePath: String

    private var phoneState: Boolean = false
    private var downloadPdf: Boolean = false

    internal enum class Action {
        NONE,
        SEND,
        OPEN
    }

    override fun getLayout(): Int {
        return R.layout.permissions_layout
    }

    override fun getTitle(): Int {
        return R.string.permissions_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        label_read_phone_state.setOnClickListener {

            phoneState = true
            downloadPdf = false

            askPhonePermission()
            showPhoneState()
        }

        label_explores_pdf.setOnClickListener {

            val intent = Intent(Intent.ACTION_VIEW)
            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            // intent.action = Intent.ACTION_VIEW
            intent.type = "application/pdf"
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(activity, "No application found", Toast.LENGTH_SHORT).show()
            }
        }

        val encodedUrl = encodeBase64Url("https://kotlinlang.org/docs/kotlin-docs.pdf")
        Log.i("TAG_PDF", "Encoded url: $encodedUrl")

        val decodedUrl = decodeBase64Url("rO0ABXQAK2h0dHBzOi8va290bGlubGFuZy5vcmcvZG9jcy9rb3RsaW4tZG9jcy5wZGY=")
        Log.i("TAG_PDF", "Decoded url: $decodedUrl")

        url = "https://kotlinlang.org/docs/kotlin-docs.pdf"
        // url = "http://www.vittal.it/wp-content/uploads/2019/07/kotlin.pdf"

        action =
            Action.NONE
        isDownloading = false

        label_download_pdf.setOnClickListener {

            phoneState = false
            downloadPdf = true

            customPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
            customPopup.setCancelable(false)
            customPopup.setTitle("")
            customPopup.setMessage("")

            customPopup.setButtons(
                resources.getString(R.string.popup_button_cancel), View.OnClickListener {
                    customPopup.dismiss()
                },
                resources.getString(R.string.popup_button_send), View.OnClickListener {
                    customPopup.dismiss()
                    action =
                        Action.SEND
                    askPDFPermission()
                    downloadPDF()
                },
                resources.getString(R.string.popup_button_open), View.OnClickListener {
                    customPopup.dismiss()
                    action =
                        Action.OPEN
                    askPDFPermission()
                    downloadPDF()
                }
            )
            customPopup.show()
        }
    }

    private fun askPhonePermission() {
        checkPhonePermission()
        if (hasPhonePermission)
            return
        PermissionManager.requestPermission(context!!, this, arrayOf(Manifest.permission.READ_PHONE_STATE))
    }

    private fun checkPhonePermission() {
        hasPhonePermission = PermissionManager.checkSelfPermission(context!!, Manifest.permission.READ_PHONE_STATE)
    }

    private fun showPhoneState() {
        if (!hasPhonePermission)
            return

        customPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
        customPopup.setCancelable(false)
        customPopup.setTitle("Stato del telefono", "")
        customPopup.setMessage(
            "Brand: " + Build.BRAND +
                    "<br><br>Manufacturer: " + Build.MANUFACTURER +
                    "<br><br>Model: " + Build.MODEL +
                    "<br><br>Product: " + Build.PRODUCT +
                    "<br><br>Device: " + Build.DEVICE +
                    "<br><br>Display: " + Build.DISPLAY +
                    "<br><br>Hardware: " + Build.HARDWARE +
                    "<br><br>ID: " + Build.ID +
                    "<br><br>Type: " + Build.TYPE +
                    "<br><br>User: " + Build.USER +
                    "<br><br>Online: " + isOnline() +
                    "<br><br>Mobile Connection: " + isOnMobileConnection() +
                    "<br><br>Wi-Fi Connection: " + isOnWiFiConnection() +
                    "<br><br>Language: " + getDeviceLanguage() +
                    "<br><br>Software Version: " + getDeviceSoftwareVersion() +
                    "<br><br>SIM Operator: " + getSimOperator() +
                    "<br><br>SIM Operator Name: " + getSimOperatorName() +
                    "<br><br>SIM Kena: " + isSimKena() +
                    "<br><br>SIM Number: " + getLine1Number() +
                    "<br><br>SIM Serial Number: " + getSimSerialNumber() +
                    "<br><br>SIM Country Iso: " + getSimCountryIso() +
                    "<br><br>Network Operator: " + getNetworkOperator() +
                    "<br><br>Network Operator Name: " + getNetworkOperatorName() +
                    "<br><br>Network Country Iso: " + getNetworkCountryIso()
        )

        customPopup.setButton(
            resources.getString(R.string.popup_button_close), View.OnClickListener {
                customPopup.dismiss()
            }
        )
        customPopup.show()
    }

    private fun askPDFPermission() {
        checkPDFPermission()
        if (hasPermission)
            return
        PermissionManager.requestPermission(
            context!!,
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            true,
            intArrayOf(R.string.permission_pdf_request),
            false,
            intArrayOf(R.string.permission_pdf_never_ask_again)
        )
    }

    private fun checkPDFPermission() {
        hasPermission = PermissionManager.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
        if (phoneState && !downloadPdf) {
            checkPhonePermission()
            showPhoneState()
        }
        if (!phoneState && downloadPdf) {
            checkPDFPermission()
            downloadPDF()
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private fun downloadPDF() {

        if (!hasPermission)
            return
        if (isDownloading)
            return

        isDownloading = true

        showProgressDialog()

        if (isPDFSupported(context!!)) {

            // The name of the downloaded file.
            fileName = when {
                url.startsWith("http://") -> url.substring(7)
                url.startsWith("https://") -> url.substring(8)
                else -> "download.pdf"
            }

            // The place where the downloaded PDF file will be put.
            filePath = File(context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/").toString()

            // Create the download request.
            val request = DownloadManager.Request(Uri.parse(url))
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            val manager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val receiver: BroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {

                    context.unregisterReceiver(this)
                    val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    val cursor = manager.query(DownloadManager.Query().setFilterById(downloadId))
                    cursor.close()

                    hideProgressDialog()
                    isDownloading = false

                    if (filePath != "") {
                        if (action == Action.SEND) {
                            send(filePath, fileName)
                        } else if (action == Action.OPEN) {
                            open(filePath, fileName)
                        }
                        action =
                            Action.NONE
                    }
                }
            }
            context?.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            manager.enqueue(request) // Enqueue the request
        }
    }

    private fun isPDFSupported(context: Context): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
        val tempFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "test.pdf")
        intent.setDataAndType(Uri.fromFile(tempFile), "application/pdf")
        return context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0
    }

    private fun send(filePath: String, fileName: String) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
        intent.putExtra(Intent.EXTRA_CC, arrayOf(""))
        val uris = ArrayList<Uri>()
        uris.add(Utils.getFileUri(File(filePath, fileName), context!!))
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        startActivity(Intent.createChooser(intent, resources.getString(R.string.send_email)))
    }

    private fun open(filePath: String, fileName: String) {
        val viewIntent = Intent()
        viewIntent.action = Intent.ACTION_VIEW
        viewIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        val apkURI = FileProvider.getUriForFile(
            context!!,
            context!!.applicationContext.packageName + ".provider",
            File(filePath, fileName)
        )
        viewIntent.setDataAndType(apkURI, "application/pdf")

        val resolved = activity!!.packageManager.queryIntentActivities(viewIntent, 0)
        if (resolved != null && resolved.size > 0)
            startActivity(viewIntent)
        else
            Toast.makeText(activity, resources.getString(R.string.no_pdf_reader), Toast.LENGTH_SHORT).show()
    }
}