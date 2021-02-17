package it.giovanni.arkivio.fragments.detail.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.Utils.Companion.setTextWebview
import kotlinx.android.synthetic.main.webview_layout.*


class WebViewFragment : DetailFragment() {

    private var viewFragment: View? = null

    private var urlGitHub = ""
    private var urlDriveW3B = ""
    private var urlDriveWAW3 = ""
    private var urlDeeplink = ""
    private var genericUrl: String? = null

    private var message: String? = null

    private val pathHtml = "file:///android_asset/cb_html/"
    private var urlHtml = ""

    override fun getTitle(): Int {

        val linkGitHub = requireArguments().getInt("link_github")
        val linkDriveW3B = requireArguments().getInt("link_drive_w3b")
        val linkDriveWAW3 = requireArguments().getInt("link_drive_waw3")
        val linkHtml = requireArguments().getInt("link_html")
        val linkWebCam = requireArguments().getInt("link_webcam")

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
        if (linkWebCam != noTitle) {
            return linkWebCam
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        TODO("Not yet implemented")
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webview.requestFocus(View.FOCUS_DOWN)
        webview.settings.javaScriptCanOpenWindowsAutomatically = true
        webview.settings.pluginState = WebSettings.PluginState.ON
        webview.settings.mediaPlaybackRequiresUserGesture = false
        webview.settings.builtInZoomControls = false
        webview.isHorizontalScrollBarEnabled = true
        webview.isVerticalScrollBarEnabled = true
        webview.settings.javaScriptEnabled = true
        webview.settings.domStorageEnabled = true
        webview.settings.useWideViewPort = true

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

            override fun onPermissionRequest(request: PermissionRequest?) {
                currentActivity.runOnUiThread {
                    request?.grant(request.resources)
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

            message = requireArguments().getString("TEXT_KEY")
            if (message != null && message!!.isNotEmpty())
                setTextWebview(webview, message!!, requireContext())
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
        return if (requireArguments().containsKey(key) && requireArguments().getString(key) != null) {
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