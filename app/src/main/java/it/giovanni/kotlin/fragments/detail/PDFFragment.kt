package it.giovanni.kotlin.fragments.detail

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import it.giovanni.kotlin.R
import it.giovanni.kotlin.customview.popup.CustomDialogPopup
import it.giovanni.kotlin.fragments.DetailFragment
import it.giovanni.kotlin.utils.PermissionManager
import it.giovanni.kotlin.utils.Utils
import it.giovanni.kotlin.utils.Utils.Companion.decodeBase64Url
import it.giovanni.kotlin.utils.Utils.Companion.encodeBase64Url
import kotlinx.android.synthetic.main.pdf_layout.*
import java.io.File
import java.util.*

class PDFFragment : DetailFragment(), PermissionManager.PermissionListener {

    private var viewFragment: View? = null
    private var isDownloading: Boolean = false
    private var hasPermission: Boolean = false
    private lateinit var customPopup: CustomDialogPopup
    private lateinit var action: Action
    private lateinit var url: String
    private lateinit var fileName: String

    internal enum class Action {
        NONE,
        SEND,
        OPEN
    }

    override fun getLayout(): Int {
        return R.layout.pdf_layout
    }

    override fun getTitle(): Int {
        return R.string.pdf_title
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
        Log.i("TAGPDF", "Encoded url: $encodedUrl")

        val decodedUrl = decodeBase64Url("rO0ABXQAK2h0dHBzOi8va290bGlubGFuZy5vcmcvZG9jcy9rb3RsaW4tZG9jcy5wZGY=")
        Log.i("TAGPDF", "Decoded url: $decodedUrl")

        url = "https://kotlinlang.org/docs/kotlin-docs.pdf"
        val list = url.split("/")

        if (list.isNotEmpty())
            fileName = list[list.size - 1]

        action = Action.NONE
        isDownloading = false

        label_open_pdf.setOnClickListener {
            customPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
            customPopup.setCancelable(false)
            customPopup.setTitle("")
            customPopup.setMessage("")

            customPopup.setButtons(
                resources.getString(R.string.popup_button_cancel), View.OnClickListener {
                    customPopup.dismiss()
                },
                resources.getString(R.string.popup_button_send), View.OnClickListener {
                    action = Action.SEND
                    askPermission()
                    download()
                    customPopup.dismiss()
                },
                resources.getString(R.string.popup_button_open), View.OnClickListener {
                    action = Action.OPEN
                    askPermission()
                    download()
                    customPopup.dismiss()
                }
            )
            customPopup.show()
        }
    }

    private fun askPermission() {
        checkPermission()
        if (hasPermission)
            return
        PermissionManager.requestPermission(
            context!!,
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            true,
            intArrayOf(R.string.permission_informativa_pdf_request),
            false,
            intArrayOf(R.string.permission_informativa_pdf_never_ask_again)
        )
    }

    private fun checkPermission() {
        hasPermission = PermissionManager.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
        checkPermission()
        download()
    }

    private fun download() {
        if (!hasPermission)
            return
        if (isDownloading)
            return
        isDownloading = true

        // showProgressDialog() // TODO: DA DECOMMENTARE

        val params = Bundle()
        params.putString("url", url)
        params.putString("fileName", fileName)

        if (action == Action.SEND) {
            send("/storage/emulated/0/Download", "CGC%20Unica_206442_24set18.pdf")
            isDownloading = false
        } else if (action == Action.OPEN) {
            open("/storage/emulated/0/Download", "Modulo%20opzioni_set18.pdf")
            isDownloading = false
        }
    }

    // filePath: /storage/emulated/0/Download
    // fileName: CGC%20Unica_206442_24set18.pdf OPPURE: Modulo%20opzioni_set18.pdf
    private fun send(filePath: String, fileName: String) {

        val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        emailIntent.type = "plain/text"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
        emailIntent.putExtra(Intent.EXTRA_CC, arrayOf(""))
        val uris = ArrayList<Uri>()
        uris.add(Utils.getFileUri(getFile(filePath, fileName), context!!))
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        startActivity(Intent.createChooser(emailIntent, resources.getString(R.string.text_billing_invoice_send_email)))
    }

    private fun getFile(filePath: String, fileName: String): File {
        return File(filePath, fileName)
    }

    // filePath: /storage/emulated/0/Download
    // fileName: CGC%20Unica_206442_24set18.pdf OPPURE: Modulo%20opzioni_set18.pdf
    private fun open(filePath: String, fileName: String) {

        val viewIntent = Intent()
        viewIntent.action = Intent.ACTION_VIEW
        viewIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        val apkURI = FileProvider.getUriForFile(context!!,
            context!!.applicationContext.packageName + ".provider",
            getFile(filePath, fileName)
        )
        viewIntent.setDataAndType(apkURI, "application/pdf")

        val resolved = activity!!.packageManager.queryIntentActivities(viewIntent, 0)
        if (resolved != null && resolved.size > 0)
            startActivity(viewIntent)
        else
            Toast.makeText(activity, resources.getString(R.string.text_billing_invoice_no_pdf_reader), Toast.LENGTH_SHORT).show()
    }
}