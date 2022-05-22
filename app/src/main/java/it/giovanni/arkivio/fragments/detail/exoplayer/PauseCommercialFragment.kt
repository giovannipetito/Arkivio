package it.giovanni.arkivio.fragments.detail.exoplayer

//import android.graphics.Bitmap
//import android.graphics.Color
//import android.net.Uri
//import android.net.http.SslError
//import android.os.Bundle
//import android.view.KeyEvent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.webkit.*
//import androidx.core.net.toUri
//import com.swisscom.tvlib.api.gt12.model.AdvertisingItem
//import com.swisscom.tvlib.api.gt12.model.Event
//import com.swisscom.tvlib.api.gt12.model.Event.GT12EventType
//import com.swisscom.tvlib.api.gt12.model.Image
//import com.swisscom.wingo.databinding.V2FragmentGt12PauseCommercialBinding
//import com.swisscom.wingo.v2.common.EXTRA_ITEM
//import com.swisscom.wingo.v2.common.HttpStatus
//import com.swisscom.wingo.v2.utils.ktx.delay
//import com.swisscom.wingo.v2.entity.Duration
//import com.swisscom.wingo.v2.entity.seconds
//import com.swisscom.wingo.v2.events.impl.HideGT12PauseFragmentEvent
//import com.swisscom.wingo.v2.events.impl.OnGt12TrackingEvent
//import com.swisscom.wingo.v2.utils.ktx.invisible
//import com.swisscom.wingo.v2.utils.ktx.visible
//import com.wingo.base.enums.ImageSize
//import com.wingo.base.utils.KeyCodes
//import io.reactivex.rxjava3.disposables.CompositeDisposable
//import io.reactivex.rxjava3.kotlin.plusAssign
//import it.sephiroth.android.library.asm.runtime.debuglog.annotations.DebugLog
//import it.sephiroth.android.library.asm.runtime.debuglog.annotations.DebugLogClass
//import timber.log.Timber
//
//class PauseCommercialFragment : GT12PlayerFragment() {
//
//    private var currentStatus: GT12EventType? = null
//
//    private var viewBinding: V2FragmentGt12PauseCommercialBinding? = null
//    private lateinit var webView: WebView
//    private val webViewClient = WebViewClientImpl()
//    private val delayedDisposables = CompositeDisposable()
//
//    val advertising: AdvertisingItem? by lazy { requireArguments().getParcelable(EXTRA_ITEM) as? AdvertisingItem }
//    val image: Image? by lazy { advertising?.media as? Image }
//
//    private val originalUri: Uri? by lazy { image?.buildFinalUri(ImageSize.H781.toString()) }
//
//    // true when the ads is completely loaded and visible
//    private var isAdsShown = false
//
//    // true when we had an error loading the media
//    private var isAdsLoadError = false
//
//    // true when the fragment is in the process of being faded out
//    private var isFadingOut = false
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        viewBinding = V2FragmentGt12PauseCommercialBinding.inflate(inflater, container, false)
//        return viewBinding!!.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        webView = viewBinding!!.webView
//        webView.setBackgroundColor(Color.TRANSPARENT)
//
//        webView.webViewClient = webViewClient
//        webView.setInitialScale(1)
//        webView.settings.loadWithOverviewMode = true
//        webView.settings.useWideViewPort = true
//        webView.isHorizontalScrollBarEnabled = false
//        webView.isVerticalScrollBarEnabled = false
//        webView.isVerticalFadingEdgeEnabled = false
//        webView.isScrollbarFadingEnabled = false
//    }
//
//    @Suppress("unused")
//    private fun clear() {
//        webView.stopLoading()
//        if (ABOUT_BLANK_PAGE != webView.originalUrl) {
//            webView.loadUrl(ABOUT_BLANK_PAGE)
//        }
//    }
//
//    @DebugLog(debugExit = false)
//    private fun load(url: String) {
//        if (webView.originalUrl != url) {
//            webView.loadUrl(url)
//        }
//    }
//
//    @DebugLog(debugExit = false)
//    override fun onStart() {
//        super.onStart()
//        view?.invisible()
//        (advertising?.media as? Image)?.let { _ -> load(originalUri.toString()) }
//    }
//
//    @DebugLog(debugExit = false)
//    override fun onDestroyView() {
//        super.onDestroyView()
//        delayedDisposables.clear()
//        if (isAdsShown && !isAdsLoadError) {
//            sendTrackingEvent(GT12EventType.Close)
//        }
//        viewBinding = null
//    }
//
//    @DebugLog(debugExit = false)
//    private fun onPageLoadFinished(url: String?, errorCode: Int?) {
//        val loadedUri: Uri? = url?.toUri()
//        Timber.v("isAdShown=$isAdsShown, isAdsLoadError=$isAdsLoadError, isFadingOut=$isFadingOut")
//
//        if (null != originalUri && url?.equals(originalUri?.toString(), true) == true) {
//            if (null != errorCode) {
//                onPageLoadError(loadedUri, errorCode)
//            } else if (!isAdsShown && !isAdsLoadError && !isFadingOut) {
//                fadeIn()
//            }
//        } else {
//            Timber.w("Loaded url is not the same as the original requested [$url != ${originalUri?.toString()}]")
//        }
//    }
//
//    @Suppress("UNUSED_PARAMETER")
//    @DebugLog(debugExit = false)
//    private fun onPageLoadError(url: Uri?, errorCode: Int) {
//        Timber.v("originalUri=$originalUri")
//        if (url == originalUri && !isAdsLoadError) {
//            isAdsLoadError = true
//            // always send a 500 error code in case pause ad cannot be loaded
//            sendTrackingEvent(GT12EventType.Error, HttpStatus.HTTP_INTERNAL_SERVER_ERROR)
//            HideGT12PauseFragmentEvent().send()
//        }
//    }
//
//    @DebugLog(debugExit = false)
//    private fun fadeIn() {
//        viewBinding?.root?.let { view ->
//            with(view) {
//                alpha = 0f
//                visible()
//                animate()
//                    .alpha(1f)
//                    .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
//                    .withEndAction {
//                        isAdsShown = true
//                        handleStatusChange(GT12EventType.CreativeView, null)
//                        handleStatusChange(GT12EventType.OverlayViewDuration, OVERLAY_VIEW_DURATION)
//                        handleStatusChange(GT12EventType.OtherAdInteraction, OTHER_AD_INTERACTION)
//                    }
//                    .start()
//            }
//        }
//    }
//
//    @DebugLog(debugExit = false)
//    override fun fadeOut(endAction: Runnable?) {
//        isFadingOut = true
//        delayedDisposables.clear()
//        currentStatus = null
//
//        viewBinding?.root?.let { view ->
//            view.animate()
//                .alpha(0f)
//                .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
//                .withEndAction {
//                    isFadingOut = false
//                    endAction?.run()
//                }
//                .start()
//        }
//    }
//
//    private fun handleStatusChange(type: GT12EventType, delay: Duration?) {
//        delay?.let { duration ->
//            delayedDisposables += delay(duration) {
//                currentStatus = type
//                sendTrackingEvent(type)
//            }
//        } ?: run {
//            currentStatus = type
//            sendTrackingEvent(type)
//        }
//    }
//
//    @DebugLog(debugExit = false)
//    private fun sendTrackingEvent(type: GT12EventType, errorCode: Int? = null) {
//        findTrackingEventByType(type)?.let { event -> OnGt12TrackingEvent(event, errorCode).send() }
//    }
//
//    private fun findTrackingEventByType(type: GT12EventType): Event? {
//        return advertising?.tracking?.events?.firstOrNull { event -> event.type == type.value }
//    }
//
//    @DebugLog(debugExit = true)
//    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent): Boolean {
//        Timber.v("isAdded=$isAdded, isAdsShown=$isAdsShown, currentStatus=$currentStatus")
//
//        if (isVisible && isAdsShown && currentStatus == GT12EventType.OtherAdInteraction) {
//            if (keyEvent.keyCode == KeyCodes.KEYCODE_BACK) {
//                HideGT12PauseFragmentEvent().send()
//                return true
//            }
//        }
//        return false
//    }
//
//    @Suppress("UNUSED_PARAMETER")
//    @DebugLog(debugExit = true)
//    override fun onKeyUp(keyCode: Int, keyEvent: KeyEvent): Boolean {
//        return false
//    }
//
//    @DebugLogClass(debugExit = false)
//    inner class WebViewClientImpl : WebViewClient() {
//        var errorCode: Int? = null
//
//        @DebugLog(debugExit = false)
//        override fun onLoadResource(view: WebView?, url: String?) {
//            super.onLoadResource(view, url)
//        }
//
//        @DebugLog(debugExit = false)
//        override fun onPageCommitVisible(view: WebView?, url: String?) {
//            super.onPageCommitVisible(view, url)
//            onPageLoadFinished(url, errorCode)
//        }
//
//        @DebugLog(debugExit = false)
//        override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
//            super.onScaleChanged(view, oldScale, newScale)
//        }
//
//        @DebugLog(debugExit = false)
//        override fun onPageFinished(view: WebView?, url: String?) {
//            super.onPageFinished(view, url)
//        }
//
//        @DebugLog(debugExit = false)
//        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//            super.onPageStarted(view, url, favicon)
//            errorCode = null
//        }
//
//        @DebugLog(debugExit = false)
//        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError) {
//            super.onReceivedError(view, request, error)
//            errorCode = error.errorCode
//        }
//
//        @DebugLog(debugExit = false)
//        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError) {
//            super.onReceivedSslError(view, handler, error)
//            errorCode = error.primaryError
//        }
//
//        @DebugLog(debugExit = false)
//        override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse) {
//            super.onReceivedHttpError(view, request, errorResponse)
//            errorCode = errorResponse.statusCode
//        }
//    }
//
//    companion object {
//        @JvmField
//        val TAG: String = PauseCommercialFragment::class.java.name
//
//        // default blank page
//        const val ABOUT_BLANK_PAGE = "about:blank"
//
//        val OVERLAY_VIEW_DURATION = 10.seconds
//
//        val OTHER_AD_INTERACTION = 120.seconds
//    }
//}