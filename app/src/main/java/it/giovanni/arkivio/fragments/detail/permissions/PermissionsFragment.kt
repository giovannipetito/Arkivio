package it.giovanni.arkivio.fragments.detail.permissions

import android.Manifest
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.core.net.toUri
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.dialog.CoreDialog
import it.giovanni.arkivio.databinding.PermissionsLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.PermissionManager
import it.giovanni.arkivio.utils.Utils
import it.giovanni.arkivio.utils.Utils.decodeBase64Url
import it.giovanni.arkivio.utils.Utils.encodeBase64Url
import it.giovanni.arkivio.utils.Utils.getDeviceLanguage
import it.giovanni.arkivio.utils.Utils.getDeviceSoftwareVersion
import it.giovanni.arkivio.utils.Utils.getLine1Number
import it.giovanni.arkivio.utils.Utils.getNetworkCountryIso
import it.giovanni.arkivio.utils.Utils.getNetworkOperator
import it.giovanni.arkivio.utils.Utils.getNetworkOperatorName
import it.giovanni.arkivio.utils.Utils.getSimCountryIso
import it.giovanni.arkivio.utils.Utils.getSimOperator
import it.giovanni.arkivio.utils.Utils.getSimOperatorName
import it.giovanni.arkivio.utils.Utils.getSimSerialNumber
import it.giovanni.arkivio.utils.Utils.isOnMobileConnection
import it.giovanni.arkivio.utils.Utils.isOnWiFiConnection
import it.giovanni.arkivio.utils.Utils.isOnline
import it.giovanni.arkivio.utils.Utils.isSimKena
import java.io.File

class PermissionsFragment : DetailFragment(), PermissionManager.PermissionListener {

    companion object {
        private var TAG: String = PermissionsFragment::class.java.simpleName
    }

    private var layoutBinding: PermissionsLayoutBinding? = null
    private val binding get() = layoutBinding

    private var isDownloading: Boolean = false
    private lateinit var coreDialog: CoreDialog
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

    override fun onActionSearch(searchString: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = PermissionsLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        askPermissions()
        binding?.labelShowWebcam?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("link_webcam", "WebCam")
            bundle.putString("GENERIC_URL", "https://www.omegle.com/")
            currentActivity.openDetail(Globals.WEB_VIEW, bundle)
        }

        binding?.labelPhoneState?.setOnClickListener {

            phoneState = true
            downloadPdf = false

            askPhonePermission()
            showPhoneState()
        }

        binding?.labelExploresPdf?.setOnClickListener {

            val intent = Intent(Intent.ACTION_VIEW)
            intent.type = "application/pdf"
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Toast.makeText(activity, "No application found", Toast.LENGTH_SHORT).show()
            }
        }

        val encodedUrl = encodeBase64Url("https://kotlinlang.org/docs/kotlin-docs.pdf")
        Log.i(TAG, "Encoded url: $encodedUrl")

        val decodedUrl = decodeBase64Url("rO0ABXQAK2h0dHBzOi8va290bGlubGFuZy5vcmcvZG9jcy9rb3RsaW4tZG9jcy5wZGY=")
        Log.i(TAG, "Decoded url: $decodedUrl")

        url = "https://kotlinlang.org/docs/kotlin-docs.pdf"
        // url = "http://www.vittal.it/wp-content/uploads/2019/07/kotlin.pdf"

        action = Action.NONE
        isDownloading = false

        binding?.labelDownloadPdf?.setOnClickListener {

            phoneState = false
            downloadPdf = true

            coreDialog = CoreDialog(currentActivity, R.style.CoreDialogTheme)
            coreDialog.setCancelable(false)
            coreDialog.setTitle("")
            coreDialog.setMessage("")

            coreDialog.setButtons(
                resources.getString(R.string.button_cancel), {
                    coreDialog.dismiss()
                },
                resources.getString(R.string.button_send), {
                    coreDialog.dismiss()
                    action =
                        Action.SEND
                    askPDFPermission()
                    downloadPDF()
                },
                resources.getString(R.string.button_open), {
                    coreDialog.dismiss()
                    action =
                        Action.OPEN
                    askPDFPermission()
                    downloadPDF()
                }
            )
            coreDialog.show()
        }
    }

    private fun askPermissions() {
        checkPermissions()
        if (checkPermissions()) {
            binding?.labelShowWebcam?.visibility = View.VISIBLE
            binding?.webcamSeparator?.visibility = View.VISIBLE
            return
        }
        PermissionManager.requestPermission(requireContext(), this, arrayOf(Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA))
    }

    private fun checkPermissions(): Boolean {
        return PermissionManager.checkSelfPermission(requireContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) &&
                PermissionManager.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) &&
                PermissionManager.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
    }

    private fun askPhonePermission() {
        checkPhonePermission()
        if (checkPhonePermission())
            return
        PermissionManager.requestPermission(requireContext(), this, arrayOf(Manifest.permission.READ_PHONE_NUMBERS))
    }

    private fun checkPhonePermission(): Boolean {
        return PermissionManager.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_NUMBERS)
    }

    private fun showPhoneState() {
        if (!checkPhonePermission())
            return

        coreDialog = CoreDialog(currentActivity, R.style.CoreDialogTheme)
        coreDialog.setCancelable(false)
        coreDialog.setTitle("Stato del telefono", "")
        coreDialog.setMessage(
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

        coreDialog.setButtons(
            resources.getString(R.string.button_close)
        ) {
            coreDialog.dismiss()
        }
        coreDialog.show()
    }

    private fun askPDFPermission() {
        checkPDFPermission()
        if (checkPDFPermission())
            return
        PermissionManager.requestPermission(
            requireContext(),
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            true,
            intArrayOf(R.string.permission_pdf_request),
            false,
            intArrayOf(R.string.permission_pdf_never_ask_again)
        )
    }

    private fun checkPDFPermission(): Boolean {
        return PermissionManager.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
        if (!phoneState && !downloadPdf) {
            checkPermissions()
            binding?.labelShowWebcam?.visibility = View.VISIBLE
            binding?.webcamSeparator?.visibility = View.VISIBLE
        }
        if (phoneState && !downloadPdf) {
            checkPhonePermission()
            showPhoneState()
        }
        if (!phoneState && downloadPdf) {
            checkPDFPermission()
            downloadPDF()
        }
    }

    private fun downloadPDF() {

        if (!checkPDFPermission())
            return
        if (isDownloading)
            return

        isDownloading = true

        showProgressDialog()

        if (isPDFSupported(requireContext())) {

            // The name of the downloaded file.
            fileName = when {
                url.startsWith("http://") -> url.substring(7)
                url.startsWith("https://") -> url.substring(8)
                else -> "download.pdf"
            }

            // The place where the downloaded PDF file will be put.
            filePath = File(context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/").toString()

            // Create the download request.
            val request = DownloadManager.Request(url.toUri())
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
                        action = Action.NONE
                    }
                }
            }
            context?.registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_NOT_EXPORTED
            )
            manager.enqueue(request) // Enqueue the request
        }
    }

    private fun isPDFSupported(context: Context): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
        val tempFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "test.pdf")
        intent.setDataAndType(Uri.fromFile(tempFile), "application/pdf")
        return context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        ).isNotEmpty()
    }

    private fun send(filePath: String, fileName: String) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
        intent.putExtra(Intent.EXTRA_CC, arrayOf(""))
        val uris = ArrayList<Uri>()
        uris.add(Utils.getFileUri(File(filePath, fileName), requireContext()))
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        startActivity(Intent.createChooser(intent, resources.getString(R.string.send_email)))
    }

    private fun open(filePath: String, fileName: String) {
        val viewIntent = Intent()
        viewIntent.action = Intent.ACTION_VIEW
        viewIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        val apkURI = FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName + ".provider",
            File(filePath, fileName)
        )
        viewIntent.setDataAndType(apkURI, "application/pdf")

        val resolved = activity?.packageManager?.queryIntentActivities(viewIntent, 0)
        if (resolved != null && resolved.isNotEmpty())
            startActivity(viewIntent)
        else
            Toast.makeText(activity, resources.getString(R.string.no_pdf_reader), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}