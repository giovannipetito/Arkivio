@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import it.giovanni.arkivio.*
import it.giovanni.arkivio.App.Companion.context
import it.giovanni.arkivio.deeplink.DeepLinkDescriptor
import it.giovanni.arkivio.fragments.*
import it.giovanni.arkivio.fragments.detail.cardio.CardIOFragment
import it.giovanni.arkivio.fragments.detail.datemanager.*
import it.giovanni.arkivio.fragments.detail.email.EmailFragment
import it.giovanni.arkivio.fragments.detail.fonts.FontsFragment
import it.giovanni.arkivio.fragments.detail.layoutmanager.LayoutManagerFragment
import it.giovanni.arkivio.fragments.detail.logcat.LogcatFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.MachineLearningFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.automlvisionedge.AutoMLVisionEdgeFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.barcodescanning.BarcodeScanningFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.facialdetection.FacialDetectionFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.imagelabeling.ImageLabelingFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.landmarksrecognition.LandmarksRecognitionFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.languageid.LanguageIDFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.objectdetectionandtracking.ObjectDetectionFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.quickanswer.QuickAnswerFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.textrecognition.TextRecognitionFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.translation.TranslationFragment
import it.giovanni.arkivio.fragments.detail.preference.PreferenceFragment
import it.giovanni.arkivio.fragments.detail.preference.PreferenceListFragment
import it.giovanni.arkivio.fragments.detail.rubrica.RubricaDetailFragment
import it.giovanni.arkivio.fragments.detail.rubrica.RubricaHomeFragment
import it.giovanni.arkivio.fragments.detail.rubrica.RubricaListFragment
import it.giovanni.arkivio.fragments.detail.webview.WebViewFragment
import it.giovanni.arkivio.fragments.detail.nearby.NearbyFragment
import it.giovanni.arkivio.fragments.detail.nearby.beacons.NearbyBeaconsFragment
import it.giovanni.arkivio.fragments.detail.nearby.chat.NearbyChatFragment
import it.giovanni.arkivio.fragments.detail.nearby.game.NearbyGameFragment
import it.giovanni.arkivio.fragments.detail.nearby.search.NearbySearchFragment
import it.giovanni.arkivio.fragments.detail.notification.NotificationFragment
import it.giovanni.arkivio.fragments.detail.permissions.PermissionsFragment
import it.giovanni.arkivio.fragments.detail.stickyheader.StickyHeaderFragment
import it.giovanni.arkivio.fragments.detail.youtube.YouTubeFragment
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.UserFactory
import it.giovanni.arkivio.utils.Utils
import it.giovanni.arkivio.utils.Utils.Companion.isOnline
import it.giovanni.arkivio.viewinterfaces.IProgressLoader
import it.giovanni.arkivio.fragments.detail.youtube.search.SearchVideoFragment
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadDarkModeStateFromPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadRememberMeFromPreferences
import java.util.*

class MainActivity : GPSActivity(), IProgressLoader {

    private val SPLASH_DISPLAY_TIME: Long = 3000
    private val DELAY_TIME: Long = 3000
    private var progressDialog: Dialog? = null
    private var spinnerLogo: ImageView? = null
    private var spinnerAnimation: AnimationDrawable? = null

    private var SPLASH_FRAGMENT: String = "SPLASH_FRAGMENT"
    private var LOGIN_FRAGMENT: String = "LOGIN_FRAGMENT"
    private var MAIN_FRAGMENT: String = "MAIN_FRAGMENT"
    private var DIALOG_FLOW_FRAGMENT: String = "DIALOG_FLOW_FRAGMENT"

    private var mainFragment: MainFragment? = null
    private var pushBundle: Bundle? = null
    private var deepLinkEvent: DeepLinkDescriptor? = null

    private var rememberMe: Boolean = false

    companion object {
        var running = false
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rememberMe = loadRememberMeFromPreferences()

        progressDialog = Dialog(this, R.style.DialogTheme)
        progressDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val view = LayoutInflater.from(applicationContext).inflate(R.layout.progress_dialog_custom, null)

        spinnerLogo = view.findViewById(R.id.spinnerImageView) as ImageView
        progressDialog?.setContentView(view)
        progressDialog?.setCancelable(true)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frame_container, SplashFragment(), SPLASH_FRAGMENT)
                .commit()

            if (!rememberMe) {
                Handler().postDelayed({
                    supportFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.frame_container, LoginFragment(), LOGIN_FRAGMENT
                        ).commit()
                }, SPLASH_DISPLAY_TIME)
            } else {
                if (isOnline()) {
                    Handler().postDelayed({
                        supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .replace(R.id.frame_container, MainFragment(), MAIN_FRAGMENT)
                            .commitAllowingStateLoss()
                            // .commit()
                    }, SPLASH_DISPLAY_TIME)
                } else
                    Toast.makeText(context,"Errore di connessione", Toast.LENGTH_LONG).show()
            }
        }

        // load user preferences
        App.getRepository()!!.loadPreferences()

        if (intent != null && intent.extras != null) {
            val params = intent.extras

            if (params != null && params.containsKey(DeepLinkDescriptor.DEEP_LINK)) {
                Log.i(TAG, "DeepLink trovato..")
                val bundle = params.getBundle(DeepLinkActivity.DEEP_LINK)
                val uri = bundle!!.getParcelable<Uri>(DeepLinkActivity.DEEP_LINK_URI)
                if (uri != null)
                    Log.i(TAG, "HOST:" + uri.host + "| path" + uri.path)
                deepLinkEvent = DeepLinkDescriptor()
                deepLinkEvent!!.deeplink = uri
            }
        }
    }

    // deeplink broadcast receiver
    private var deepLinkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            if (intent != null) {
                if (intent.action == DeepLinkDescriptor.DEEP_LINK_ACTION) {
                    deepLinkEvent = DeepLinkDescriptor()
                    deepLinkEvent!!.deeplink = intent.extras!!.getParcelable(DeepLinkDescriptor.DEEP_LINK_URI)
                }
            }
        }
    }

    private fun openDeepLink(uri: Uri) {

        val host = uri.host
        when (host?.toLowerCase(Locale.ITALIAN)) {
            DeepLinkDescriptor.URI_GC3 -> {
                requestGPSPermission()
            }
            DeepLinkDescriptor.URI_CINEMA -> {
                requestGPSPermission()
            }
            DeepLinkDescriptor.URI_CONTACTS -> {
                openDetail(Globals.RUBRICA_HOME, null)
            }
            DeepLinkDescriptor.URI_OPENAPP -> {
                if (uri.pathSegments != null && uri.pathSegments.size > 0)
                    Utils.openApp(this, uri.pathSegments[0])
            }
            // WebView
            DeepLinkDescriptor.URI_VIEW -> {
                if (uri.query != null) {
                    val list = uri.query!!.split("url=")
                    if (list.isNotEmpty()) {
                        val url = list[1]
                        val bundle = Bundle()
                        var query = "num=" + UserFactory.getInstance().matricola
                        query += "&mail=" + UserFactory.getInstance().email
                        bundle.putString("GENERIC_URL", "$url?$query")
                        openDetail(Globals.WEB_VIEW, bundle)
                    }
                }
            }
            // WebView
            DeepLinkDescriptor.URI_CALL -> {
                if (uri.query != null) {
                    val list = uri.query!!.split("url=")
                    if (list.isNotEmpty()) {
                        val url = list[1]
                        val bundle = Bundle()
                        bundle.putString("GENERIC_URL", url)
                        openDetail(Globals.WEB_VIEW, bundle)
                    }
                }
            }
            DeepLinkDescriptor.URI_LOGOUT -> {
                logout()
            }
            DeepLinkDescriptor.URI_HOME_PAGE -> {
                removeAllFragmentsToMainFragment()
                Handler().postDelayed({
                    if (mainFragment != null)
                        mainFragment!!.goToHomePosition(1)
                }, 1000)
            }
            DeepLinkDescriptor.URI_HOME_WORK_PAGE -> {
                removeAllFragmentsToMainFragment()
                Handler().postDelayed({
                    if (mainFragment != null)
                        mainFragment!!.goToHomePosition(2)
                }, 1000)
            }
            DeepLinkDescriptor.URI_HOME_ADMIN_PAGE -> {
                removeAllFragmentsToMainFragment()
                Handler().postDelayed({
                    if (mainFragment != null)
                        mainFragment!!.goToHomePosition(3)
                }, 1000)
            }
        }
    }

    private fun removeAllFragmentsToMainFragment() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment != null && fragment !is MainFragment)
                supportFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }

    override fun onStart() {
        super.onStart()
        running = true
    }

    override fun onStop() {
        super.onStop()
        running = false
    }

    override fun showProgressDialog() {
        if (!progressDialog!!.isShowing) {
            progressDialog?.show()
            spinnerAnimation = (spinnerLogo?.background as AnimationDrawable)
            spinnerLogo?.post {
                try {
                    spinnerAnimation!!.start()
                } catch (e: Exception) {
                }
            }
        }
    }

    // Questo è il metodo hideProgressDialog originale, non quello col metodo postDelayed.
    /*
    override fun hideProgressDialog() {
        try {
            progressDialog?.dismiss()
            if (spinnerAnimation != null)
                spinnerAnimation?.stop()
        } catch (e: Exception) {
        }
    }
    */

    override fun hideProgressDialog() {
        Handler().postDelayed({
            try {
                progressDialog?.dismiss()
                if (spinnerAnimation != null)
                    spinnerAnimation?.stop()
            } catch (e: Exception) {
            }
        }, DELAY_TIME)
    }

    fun openMainFragment() {
        removeAllFragments()
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.right_to_left, R.anim.left_to_right)
            .add(R.id.frame_container, MainFragment(), MAIN_FRAGMENT)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(R.id.frame_container)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }

    private fun removeAllFragments() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment != null)
                supportFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }

    fun logout() {
        removeAllFragments()
        val loginFragment = LoginFragment()
        loginFragment.arguments = Bundle()
        UserFactory.getInstance().clear()
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.frame_container, loginFragment, LOGIN_FRAGMENT)
            .commit()
    }

    override fun openDetail(detailType: String, extraParams: Bundle?) {
        openDetail(detailType, extraParams, null, null)
    }

    override fun openDetail(detailType: String, extraParams: Bundle?, caller: Fragment?, requestCode: Int?) {

        setStatusBarTransparent()

        var baseFragment: BaseFragment? = null

        when (detailType) {

            Globals.LOGCAT_PROJECTS -> {
                baseFragment =
                    LogcatFragment()
            }
            Globals.DATE -> {
                baseFragment = DateFragment()
            }
            Globals.DATE_FORMAT -> {
                baseFragment = DateFormatFragment()
            }
            Globals.DATE_PICKER -> {
                baseFragment = DatePickerFragment()
            }
            Globals.CALENDARVIEW_HORIZONTAL -> {
                baseFragment = CalendarViewHorizontalFragment()
            }
            Globals.CALENDARVIEW_VERTICAL -> {
                baseFragment = CalendarViewVerticalFragment()
            }
            Globals.SMARTWORKING -> {
                baseFragment = SmartworkingFragment()
            }
            Globals.EMAIL -> {
                baseFragment = EmailFragment()
            }
            Globals.RUBRICA_HOME -> {
                baseFragment = RubricaHomeFragment()
            }
            Globals.RUBRICA_LIST -> {
                baseFragment = RubricaListFragment()
            }
            Globals.RUBRICA_DETAIL -> {
                baseFragment = RubricaDetailFragment()
            }
            Globals.WEB_VIEW -> {
                baseFragment = WebViewFragment()
            }
            Globals.PERMISSIONS -> {
                baseFragment =
                    PermissionsFragment()
            }
            Globals.LAYOUT_MANAGER -> {
                baseFragment =
                    LayoutManagerFragment()
            }
            Globals.DIALOG_FLOW -> {
                baseFragment = DialogFlowFragment()
            }
            Globals.PREFERENCE -> {
                baseFragment = PreferenceFragment()
            }
            Globals.PREFERENCE_LIST -> {
                baseFragment = PreferenceListFragment()
            }
            Globals.STICKY_HEADER -> {
                baseFragment =
                    StickyHeaderFragment()
            }
            Globals.STICKY_HEADER -> {
                baseFragment =
                    StickyHeaderFragment()
            }
            Globals.CARD_IO -> {
                baseFragment =
                    CardIOFragment()
            }
            Globals.YOUTUBE_MANAGER -> {
                baseFragment =
                    YouTubeFragment()
            }
            Globals.SEARCH_VIDEO -> {
                baseFragment = SearchVideoFragment()
            }
            Globals.FONTS -> {
                baseFragment =
                    FontsFragment()
            }
            Globals.NOTIFICATION -> {
                baseFragment = NotificationFragment()
            }
            Globals.NEARBY -> {
                baseFragment = NearbyFragment()
            }
            Globals.NEARBY_SEARCH -> {
                baseFragment = NearbySearchFragment()
            }
            Globals.NEARBY_CHAT -> {
                baseFragment = NearbyChatFragment()
            }
            Globals.NEARBY_GAME -> {
                baseFragment = NearbyGameFragment()
            }
            Globals.NEARBY_BEACONS -> {
                baseFragment = NearbyBeaconsFragment()
            }
            Globals.MACHINE_LEARNING -> {
                baseFragment = MachineLearningFragment()
            }
            Globals.TEXT_RECOGNITION -> {
                baseFragment = TextRecognitionFragment()
            }
            Globals.IMAGE_LABELING -> {
                baseFragment = ImageLabelingFragment()
            }
            Globals.FACIAL_DETECTION -> {
                baseFragment = FacialDetectionFragment()
            }
            Globals.OBJECT_DETECTION -> {
                baseFragment = ObjectDetectionFragment()
            }
            Globals.BARCODE_SCANNING -> {
                baseFragment = BarcodeScanningFragment()
            }
            Globals.LANGUAGE_ID -> {
                baseFragment = LanguageIDFragment()
            }
            Globals.TRANSLATION -> {
                baseFragment = TranslationFragment()
            }
            Globals.QUICK_ANSWER -> {
                baseFragment = QuickAnswerFragment()
            }
            Globals.AUTOML_VISION_EDGE -> {
                baseFragment = AutoMLVisionEdgeFragment()
            }
            Globals.LANDMARKS_RECOGNITION -> {
                baseFragment = LandmarksRecognitionFragment()
            }
        }

        if (baseFragment != null) {
            if (supportFragmentManager.findFragmentByTag(baseFragment.javaClass.canonicalName) != null) {
                return
            }
            if (caller != null && requestCode != null) {
                baseFragment.setTargetFragment(caller, requestCode)
            }
            if (extraParams != null) {
                baseFragment.arguments = extraParams
            }
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.right_to_left, R.anim.left_to_right)
                .add(R.id.frame_container, baseFragment, baseFragment.javaClass.canonicalName)
                .commit()
        }
    }

    override fun openDialogDetail(detailType: String, extraParams: Bundle?) {
        openDialogDetail(detailType, extraParams, null, null)
    }

    override fun openDialogDetail(detailType: String, extraParams: Bundle?, caller: Fragment?, requestCode: Int?) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.frame_container, DialogFlowFragment(), DIALOG_FLOW_FRAGMENT)
            .commit()
    }

    override fun onBackPressed() {
        setDarkModeStatusBarTransparent()
        val frameLayout: FrameLayout = findViewById(R.id.frame_container)
        val baseFragment: BaseFragment = (supportFragmentManager.findFragmentById(frameLayout.id) as BaseFragment)
        if (supportFragmentManager.findFragmentById(frameLayout.id) is DetailFragment) {
            if (!(baseFragment as DetailFragment).isUserSearch() && !(baseFragment).closeAction()) {

                fragmentsTransition(baseFragment)

            } else if ((baseFragment).closeAction()) {
                if ((baseFragment).beforeClosing()) {
                    fragmentsTransition(baseFragment)
                }
            } else {
                (baseFragment).closeSearch()
            }
        } else if (supportFragmentManager.findFragmentById(frameLayout.id) is BaseFragment &&
            baseFragment.getSectionType() == BaseFragment.SectionType.DIALOG_FLOW) {
            dialogFlowFragmentTransition(baseFragment)

        } else if (supportFragmentManager.findFragmentById(frameLayout.id) is MainFragment) {
            val layout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
            if (layout.isDrawerOpen(GravityCompat.END)) {
                layout.closeDrawer(GravityCompat.END)
                // NOTA: GravityCompat.START se il menu laterale fosse a sinistra.
            } else {
                super.onBackPressed()
            }
        }
        else
            super.onBackPressed()
    }

    private fun fragmentsTransition(baseFragmentView: DetailFragment) {
        if (baseFragmentView.targetFragment != null) {
            baseFragmentView.targetFragment!!.onActivityResult(
                baseFragmentView.targetRequestCode,
                Activity.RESULT_OK,
                baseFragmentView.getResultBack()
            )
        }
        val transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.popBackStackImmediate(baseFragmentView.javaClass.name, 1)
        transaction.setCustomAnimations(
            R.anim.out_left_to_right,
            R.anim.out_right_to_left
        )
        transaction.remove(baseFragmentView).commit()
    }

    private fun dialogFlowFragmentTransition(baseFragmentView: BaseFragment) {
        if (baseFragmentView.targetFragment != null) {
            baseFragmentView.targetFragment!!.onActivityResult(
                baseFragmentView.targetRequestCode,
                Activity.RESULT_OK,
                Intent()
            )
        }
        val transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.popBackStackImmediate(baseFragmentView.javaClass.name, 1)
        // transaction.setCustomAnimations(R.anim.out_left_to_right, R.anim.out_right_to_left)
        transaction.remove(baseFragmentView).commit()
    }

    fun setStatusBarTransparent() {
        window.statusBarColor = Color.TRANSPARENT // Oppure: ContextCompat.getColor(App.context, android.R.color.transparent)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    fun setDarkModeStatusBarTransparent() {
        window.statusBarColor = Color.TRANSPARENT
        val isDarkMode = loadDarkModeStateFromPreferences()
        if (isDarkMode)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Set text light.
        else
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // Set text dark.
    }

    override fun onResume() {
        super.onResume()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_container)
        if (currentFragment !is LoginFragment) {
            // presenter!!.checkKeepAlive()
        }

        // check if is resumed by a push
        if (pushBundle != null) {
            /*
            if (pushBundle!!.containsKey(EmployeeFirebaseMessagingService.KEY_CAMPAIGN_ID)) {
                openDetail(Globals.FRAGMENT_GREEN_GRASS_DETAIL, pushBundle, mainFragment, Globals.REQUEST_CODE_GREEN_GRASS_DETAIL)
            }
            */
            pushBundle = null
        } else {
            if (UserFactory.getInstance().isLogged) {
                // manage deeplink
                if (deepLinkEvent != null && deepLinkEvent!!.deeplink != null) {
                    openDeepLink(deepLinkEvent!!.deeplink!!)
                    clearDeepLinkEvent()
                }
            }
        }
    }

    fun checkDeepLinkEvent(): Boolean {
        return deepLinkEvent != null
    }

    private fun clearDeepLinkEvent() {
        deepLinkEvent = null
    }

    fun getDeepLinkEvent(): DeepLinkDescriptor? {
        return deepLinkEvent
    }

    fun openApp(packageName: String) {
        Utils.openApp(this, packageName)
    }

    fun openBrowser(url: String) {
        Utils.openBrowser(this, url)
    }
}