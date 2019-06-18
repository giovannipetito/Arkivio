package it.giovanni.kotlin.fragments.detail.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.DetailFragment
import kotlinx.android.synthetic.main.webview_layout.*

class WebViewFragment : DetailFragment() {

    private var urlGitHub = ""
    private var urlDriveW3B = ""
    private var urlDriveWAW3 = ""

    private var urlDeeplink = ""

    private var genericUrl:String? = null

    override fun getTitle(): Int {

        val linkGitHub = arguments!!.getInt("link_github")
        val linkDriveW3B = arguments!!.getInt("link_drive_w3b")
        val linkDriveWAW3 = arguments!!.getInt("link_drive_waw3")

        val noTitle = 0

        if (linkGitHub != noTitle) {
            return linkGitHub
        }
        if (linkDriveW3B != noTitle) {
            return linkDriveW3B
        }
        if (linkDriveWAW3 != noTitle) {
            return linkDriveWAW3
        }

        return -1
    }

    override fun getLayout(): Int {
        return R.layout.webview_layout
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return false
    }

    override fun closeAction(): Boolean {
        return true
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webview.requestFocus(View.FOCUS_DOWN)
        webview.settings.javaScriptEnabled = true
        webview.settings.javaScriptCanOpenWindowsAutomatically = true
        webview.settings.useWideViewPort = true
        webview.settings.builtInZoomControls = false
        webview.settings.builtInZoomControls = false

        webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {

                if (progressBar != null) {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = 0
                    progressBar.max = 100
                    progressBar.progress = progress

                    if (progress == 100) {
                        progressBar.progress = 0
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }
        if (arguments != null) {

            urlGitHub = getUrl("url_github")
            urlDriveW3B = getUrl("url_drive_w3b")
            urlDriveWAW3 = getUrl("url_drive_waw3")

            urlDeeplink = getUrl("url_deeplink")

            genericUrl = getUrl("GENERIC_URL") //from deeplink

        }

        when {
            urlGitHub != "" -> {
                webview.loadUrl(urlGitHub)
            }
            urlDriveW3B != "" -> {
                webview.loadUrl(urlDriveW3B)
            }
            urlDriveWAW3 != "" -> {
                webview.loadUrl(urlDriveWAW3)
            }
            urlDeeplink != "" -> webview.loadUrl(urlDeeplink)
            genericUrl != null -> webview.loadUrl(genericUrl)
        }
    }

    private fun getUrl(key: String): String {
        return if (arguments!!.containsKey(key) && arguments!!.getString(key) != null) {
            arguments?.getString(key)!!
        } else ""
    }
}