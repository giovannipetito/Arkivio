package it.giovanni.arkivio.fragments.detail.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import it.giovanni.arkivio.R
import kotlinx.android.synthetic.main.webview_video_layout.*

class WebViewActivity: AppCompatActivity() {

    private var bundleVideo: Bundle = Bundle()
    private var titleVideo: String? = null
    private var urlVideo: String? = null

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_video_layout)

        bundleVideo = intent.getBundleExtra("bundle_video")

        titleVideo =
            if (bundleVideo.getString("link_video") != null)
                bundleVideo.getString("link_video")
            else ""

        urlVideo =
            if (bundleVideo.getString("url_video") != null)
                bundleVideo.getString("url_video")
            else ""

        when {
            urlVideo != "" -> {
                webview.loadUrl(urlVideo)
            }
        }

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

        webview.webChromeClient = object : WebChromeClient() {

            private var view: View? = null
            private var viewCallback: CustomViewCallback? = null
            private var initOrientation = 0
            private var initSystemUiVisibility = 0

            override fun onHideCustomView() {
                (window.decorView as FrameLayout).removeView(view)
                view = null
                window.decorView.systemUiVisibility = initSystemUiVisibility
                requestedOrientation = initOrientation
                viewCallback?.onCustomViewHidden()
                viewCallback = null
            }

            override fun onShowCustomView(paramView: View?, paramCustomViewCallback: CustomViewCallback?) {
                if (view != null) {
                    onHideCustomView()
                    return
                }
                view = paramView
                initSystemUiVisibility = window.decorView.systemUiVisibility
                initOrientation = requestedOrientation
                viewCallback = paramCustomViewCallback
                (window.decorView as FrameLayout).addView(view, FrameLayout.LayoutParams(-1, -1))
                window.decorView.systemUiVisibility = 3846
            }

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

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                // title = view?.title
                if (progressBar != null)
                    progressBar.visibility = View.GONE
                super.onPageFinished(view, url)
            }

            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                /*
                if (errorResponse != null) {
                    val statusCode = errorResponse.statusCode
                    val params = Bundle()
                    params.putString(Mapping.WAW3Key.WAW3_TYPE, Mapping.WAW3KeyValue.ERROR_HTTP_STATUS_TYPE)
                    params.putInt(Mapping.WAW3Key.WAW3_ERROR_CODE, statusCode)
                    params.putString(Mapping.WAW3Key.WAW3_SECTION, getUrl())
                    trackError(params)
                    gAnalytics.sendEvent(Mapping.GAnalyticsKey.CATEGORY_ERROR, "Opening_WebView", getUrl() + "|" + statusCode, null)
                }
                */
                super.onReceivedHttpError(view, request, errorResponse)
            }
        }

        arrow_go_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.out_left_to_right_2, R.anim.out_right_to_left_2)
    }
}