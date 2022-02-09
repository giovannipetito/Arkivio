package it.giovanni.arkivio.fragments.detail.webview

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.WebviewVideoLayoutBinding

class WebViewActivity: AppCompatActivity() {

    private var layoutBinding: WebviewVideoLayoutBinding? = null
    val binding get() = layoutBinding

    private var bundleVideo: Bundle = Bundle()
    private var titleVideo: String? = null
    private var urlVideo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layoutBinding = WebviewVideoLayoutBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        bundleVideo = intent.getBundleExtra("bundle_video")!!

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
                binding?.webview?.loadUrl(urlVideo!!)
            }
        }

        binding?.webview?.requestFocus(View.FOCUS_DOWN)
        binding?.webview?.settings?.javaScriptCanOpenWindowsAutomatically = true
        binding?.webview?.settings?.pluginState = WebSettings.PluginState.ON
        binding?.webview?.settings?.mediaPlaybackRequiresUserGesture = false
        binding?.webview?.settings?.builtInZoomControls = false
        binding?.webview?.isHorizontalScrollBarEnabled = true
        binding?.webview?.isVerticalScrollBarEnabled = true
        binding?.webview?.settings?.javaScriptEnabled = true
        binding?.webview?.settings?.domStorageEnabled = true
        binding?.webview?.settings?.useWideViewPort = true

        binding?.webview?.webChromeClient = object : WebChromeClient() {

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

                if (binding?.progressBar != null) {
                    binding?.progressBar?.visibility = View.VISIBLE
                    binding?.progressBar?.progress = 0
                    binding?.progressBar?.max = 100
                    binding?.progressBar?.progress = progress

                    if (progress == 100) {
                        binding?.progressBar?.progress = 0
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                runOnUiThread {
                    request?.grant(request.resources)
                }
            }
        }

        binding?.webview?.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                // title = view?.title
                if (binding?.progressBar != null)
                    binding?.progressBar?.visibility = View.GONE
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

        binding?.arrowGoBack?.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.out_left_to_right_2, R.anim.out_right_to_left_2)
    }

    override fun onDestroy() {
        super.onDestroy()
        layoutBinding = null
    }
}