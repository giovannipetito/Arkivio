package it.giovanni.arkivio.fragments.detail.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.Utils.Companion.setTextWebview
import kotlinx.android.synthetic.main.webview_layout.*


class WebViewFragment : DetailFragment() {

    private var urlGitHub = ""
    private var urlDriveW3B = ""
    private var urlDriveWAW3 = ""
    private var urlDeeplink = ""
    private var genericUrl: String? = null

    private var message: String? = null

    private val pathHtml = "file:///android_asset/cb_html/"
    private var urlHtml = ""

    override fun getTitle(): Int {

        val linkGitHub = arguments!!.getInt("link_github")
        val linkDriveW3B = arguments!!.getInt("link_drive_w3b")
        val linkDriveWAW3 = arguments!!.getInt("link_drive_waw3")

        val linkHtml = arguments!!.getInt("link_html")

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
        if (linkHtml != noTitle) {
            return linkHtml
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

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webview.requestFocus(View.FOCUS_DOWN)
        webview.settings.javaScriptEnabled = true
        webview.settings.javaScriptCanOpenWindowsAutomatically = true
        webview.settings.useWideViewPort = true
        webview.settings.builtInZoomControls = false
        webview.isVerticalScrollBarEnabled = true
        webview.isHorizontalScrollBarEnabled = true
        webview.settings.domStorageEnabled = true

        webview.settings.textSize = WebSettings.TextSize.NORMAL // Definisce la size del testo del file HTML.

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.i("TAG_HTML", "Url: $url") // market://details?id=it.wind.windtre
                try {
                    if (url.startsWith("market")) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } else currentActivity.onBackPressed()
                } catch (e: Exception) {
                    currentActivity.onBackPressed()
                }
                return true
            }
        }

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

        webview.webViewClient = object : WebViewClient() {
            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                if (errorResponse != null) {
                    /*
                    val statusCode = errorResponse.statusCode
                    val params = Bundle()
                    params.putString(Mapping.WAW3Key.WAW3_TYPE, Mapping.WAW3KeyValue.ERROR_HTTP_STATUS_TYPE)
                    params.putInt(Mapping.WAW3Key.WAW3_ERROR_CODE, statusCode)
                    params.putString(Mapping.WAW3Key.WAW3_SECTION, getUrl())
                    trackError(params)
                    gAnalytics.sendEvent(Mapping.GAnalyticsKey.CATEGORY_ERROR, "Opening_WebView", getUrl() + "|" + statusCode, null)
                    */
                }
                super.onReceivedHttpError(view, request, errorResponse)
            }
        }

        if (arguments != null) {

            urlGitHub = getUrl("url_github")
            urlDriveW3B = getUrl("url_drive_w3b")
            urlDriveWAW3 = getUrl("url_drive_waw3")
            urlDeeplink = getUrl("url_deeplink")
            genericUrl = getUrl("GENERIC_URL") // from deeplink

            urlHtml = getUrl("url_html")

            message = arguments!!.getString("TEXT_KEY")
            if (message != null && message!!.isNotEmpty())
                setTextWebview(webview, message!!, context!!)
        }

        if (message == null) {
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

                urlHtml != "" -> {
                    webview.loadUrl(pathHtml + urlHtml)
                }

                genericUrl != null -> webview.loadUrl(genericUrl)
            }
        }
    }

    private fun getUrl(key: String): String {
        return if (arguments!!.containsKey(key) && arguments!!.getString(key) != null) {
            arguments?.getString(key)!!
        } else ""
    }

    private fun getUrl(): String {
        return when {
            urlGitHub != "" -> urlGitHub
            urlDriveW3B != "" -> urlDriveW3B
            urlDriveWAW3 != "" -> urlDriveWAW3
            urlDeeplink != "" -> urlDeeplink
            urlHtml != "" -> urlHtml
            else -> ""
        }
    }
}