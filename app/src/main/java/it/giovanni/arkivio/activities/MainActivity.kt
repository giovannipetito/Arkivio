package it.giovanni.arkivio.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import it.giovanni.arkivio.databinding.ActivityMainBinding
import it.giovanni.arkivio.databinding.ProgressDialogCustomBinding
import it.giovanni.arkivio.deeplink.DeepLinkDescriptor
import it.giovanni.arkivio.fragments.*
import it.giovanni.arkivio.fragments.detail.cardio.CardIOFragment
import it.giovanni.arkivio.fragments.detail.client.AsyncHttpFragment
import it.giovanni.arkivio.fragments.detail.client.SimpleRetrofitFragment
import it.giovanni.arkivio.fragments.detail.client.VolleyFragment
import it.giovanni.arkivio.fragments.detail.datemanager.*
import it.giovanni.arkivio.fragments.detail.email.EmailFragment
import it.giovanni.arkivio.fragments.detail.exoplayer.ExoPlayerFragment
import it.giovanni.arkivio.fragments.detail.fonts.FontsFragment
import it.giovanni.arkivio.fragments.detail.layoutmanager.LayoutManagerFragment
import it.giovanni.arkivio.fragments.detail.logcat.LogcatFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.MachineLearningFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.facialdetection.FacialDetectionFragment
import it.giovanni.arkivio.fragments.detail.machinelearning.textrecognition.TextRecognitionFragment
import it.giovanni.arkivio.fragments.detail.preference.PreferenceFragment
import it.giovanni.arkivio.fragments.detail.preference.PreferenceListFragment
import it.giovanni.arkivio.fragments.detail.rubrica.RubricaDetailFragment
import it.giovanni.arkivio.fragments.detail.rubrica.RubricaHomeFragment
import it.giovanni.arkivio.fragments.detail.rubrica.RubricaListFragment
import it.giovanni.arkivio.fragments.detail.webview.WebViewFragment
import it.giovanni.arkivio.fragments.detail.notification.NotificationFragment
import it.giovanni.arkivio.fragments.detail.notification.NotificationHomeFragment
import it.giovanni.arkivio.fragments.detail.permissions.PermissionsFragment
import it.giovanni.arkivio.fragments.detail.puntonet.PuntoNetFragment
import it.giovanni.arkivio.fragments.detail.puntonet.coroutines.*
import it.giovanni.arkivio.fragments.detail.puntonet.dependencyinjection.DependencyInjectionFragment
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.UsersFragment
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.UsersDetailFragment
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.UsersHomeFragment
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.UsersPagingFragment
import it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.logininput.LoginInputFragment
import it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.logininput.LoginResultFragment
import it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.userinput.UserInputFragment
import it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.users.MvvmUsersFragment
import it.giovanni.arkivio.fragments.detail.puntonet.paging.RickMortyFragment
import it.giovanni.arkivio.fragments.detail.puntonet.reactivex.RxExample1Fragment
import it.giovanni.arkivio.fragments.detail.puntonet.reactivex.RxExample2Fragment
import it.giovanni.arkivio.fragments.detail.puntonet.reactivex.RxExample3Fragment
import it.giovanni.arkivio.fragments.detail.puntonet.reactivex.RxExample4Fragment
import it.giovanni.arkivio.fragments.detail.puntonet.reactivex.RxHomeFragment
import it.giovanni.arkivio.fragments.detail.puntonet.reactivex.RxRetrofitFragment
import it.giovanni.arkivio.fragments.detail.stickyheader.StickyHeaderFragment
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.UserFactory
import it.giovanni.arkivio.utils.Utils
import it.giovanni.arkivio.utils.Utils.Companion.isOnline
import it.giovanni.arkivio.viewinterfaces.IProgressLoader
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadDarkModeStateFromPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadRememberMeFromPreferences

class MainActivity : BaseActivity(), IProgressLoader {

    companion object {
        var running = false
    }

    private var layoutBinding: ActivityMainBinding? = null
    val binding: ActivityMainBinding? get() = layoutBinding

    private val delayTime1: Long = 1000
    private val delayTime2: Long = 3000
    private var progressDialog: Dialog? = null
    private var spinnerLogo: ImageView? = null
    private var spinnerAnimation: AnimationDrawable? = null

    private var mSplashFragment: String = "SPLASH_FRAGMENT"
    private var mLoginFragment: String = "LOGIN_FRAGMENT"
    private var mMainFragment: String = "MAIN_FRAGMENT"
    private var mDialogFlowFragment: String = "DIALOG_FLOW_FRAGMENT"

    private var mainFragment: MainFragment? = null
    private var pushBundle: Bundle? = null
    private var deepLinkEvent: DeepLinkDescriptor? = null

    private var rememberMe: Boolean = false

    private var handler1: Handler? = null
    private var handler2: Handler? = null
    private var handler3: Handler? = null
    private var handler4: Handler? = null
    private var handler5: Handler? = null
    private var handler6: Handler? = null

    private val m1Runnable: Runnable = Runnable {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.frame_container, LoginFragment(), mLoginFragment
            ).commit()
    }

    private val m2Runnable: Runnable = Runnable {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.frame_container, MainFragment(), mMainFragment)
            .commitAllowingStateLoss()
            // .commit()
    }

    private val m3Runnable: Runnable = Runnable {
        if (mainFragment != null)
            mainFragment?.goToHomePosition(1)
    }

    private val m4Runnable: Runnable = Runnable {
        if (mainFragment != null)
            mainFragment?.goToHomePosition(2)
    }

    private val m5Runnable: Runnable = Runnable {
        if (mainFragment != null)
            mainFragment?.goToHomePosition(3)
    }

    private val m6Runnable: Runnable = Runnable {
        try {
            progressDialog?.dismiss()
            if (spinnerAnimation != null)
                spinnerAnimation?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layoutBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        handler1 = Handler(Looper.getMainLooper())
        handler2 = Handler(Looper.getMainLooper())
        handler3 = Handler(Looper.getMainLooper())
        handler4 = Handler(Looper.getMainLooper())
        handler5 = Handler(Looper.getMainLooper())
        handler6 = Handler(Looper.getMainLooper())

        // Room: Load user preferences.
        App.getRepository()?.loadPreferences()

        rememberMe = loadRememberMeFromPreferences()

        progressDialog = Dialog(this, R.style.DialogTheme)
        progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val progressDialogCustomBinding = ProgressDialogCustomBinding.inflate(LayoutInflater.from(context))
        val view = progressDialogCustomBinding.root

        spinnerLogo = progressDialogCustomBinding.spinnerImageView
        progressDialog?.setContentView(view)
        progressDialog?.setCancelable(true)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frame_container, SplashFragment(), mSplashFragment)
                .commit()

            if (!rememberMe)
                handler1?.postDelayed(m1Runnable, delayTime2)
            else {
                if (isOnline())
                    handler2?.postDelayed(m2Runnable, delayTime2)
                else
                    Toast.makeText(context,"Errore di connessione", Toast.LENGTH_LONG).show()
            }
        }

        if (intent != null && intent.extras != null) {
            val params = intent.extras

            if (params != null && params.containsKey(DeepLinkDescriptor.DEEP_LINK)) {
                Log.i(TAG, "DeepLink trovato..")
                val bundle = params.getBundle(DeepLinkActivity.DEEP_LINK)
                val uri = bundle?.getParcelable<Uri>(DeepLinkActivity.DEEP_LINK_URI)
                if (uri != null)
                    Log.i(TAG, "HOST:" + uri.host + "| path" + uri.path)
                deepLinkEvent = DeepLinkDescriptor()
                deepLinkEvent?.deeplink = uri
            }
        }
    }

    private fun openDeepLink(uri: Uri) {

        val host = uri.host
        when (host?.lowercase()) {
            DeepLinkDescriptor.URI_CONTACTS -> {
                openDetail(Globals.RUBRICA_REALTIME, null)
            }
            DeepLinkDescriptor.URI_OPENAPP -> {
                if (uri.pathSegments != null && uri.pathSegments.size > 0)
                    Utils.openApp(this, uri.pathSegments[0])
            }
            // WebView
            DeepLinkDescriptor.URI_VIEW -> {
                if (uri.query != null) {
                    val list = uri.query?.split("url=")
                    if (list?.isNotEmpty()!!) {
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
                    val list = uri.query?.split("url=")
                    if (list?.isNotEmpty()!!) {
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
                handler3?.postDelayed(m3Runnable, delayTime1)
            }
            DeepLinkDescriptor.URI_WORKING_AREA -> {
                removeAllFragmentsToMainFragment()
                handler4?.postDelayed(m4Runnable, delayTime1)
            }
            DeepLinkDescriptor.URI_LINK_AREA -> {
                removeAllFragmentsToMainFragment()
                handler5?.postDelayed(m5Runnable, delayTime1)
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
        if (!progressDialog?.isShowing!!) {
            progressDialog?.show()
            spinnerAnimation = (spinnerLogo?.background as AnimationDrawable)
            spinnerLogo?.post {
                try {
                    spinnerAnimation?.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun hideProgressDialog() {
        handler6?.postDelayed(m6Runnable, delayTime2)
    }

    fun openMainFragment() {
        removeAllFragments()
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.right_to_left, R.anim.left_to_right)
            .add(R.id.frame_container, MainFragment(), mMainFragment)
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
            .replace(R.id.frame_container, loginFragment, this.mLoginFragment)
            .commit()
    }

    override fun openDetail(detailType: String, extraParams: Bundle?) {
        openDetail(detailType, extraParams, null, null)
    }

    override fun openDetail(detailType: String, extraParams: Bundle?, caller: Fragment?, requestCode: Int?) {

        var baseFragment: BaseFragment? = null

        when (detailType) {

            Globals.LOGCAT_PROJECTS -> {
                baseFragment = LogcatFragment()
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
            Globals.SIMPLE_RETROFIT -> {
                baseFragment = SimpleRetrofitFragment()
            }
            Globals.ASYNC_HTTP -> {
                baseFragment = AsyncHttpFragment()
            }
            Globals.VOLLEY -> {
                baseFragment = VolleyFragment()
            }
            Globals.RUBRICA_REALTIME -> {
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
                baseFragment = PermissionsFragment()
            }
            Globals.LAYOUT_MANAGER -> {
                baseFragment = LayoutManagerFragment()
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
                baseFragment = StickyHeaderFragment()
            }
            Globals.CARD_IO -> {
                baseFragment = CardIOFragment()
            }
            Globals.FONTS -> {
                baseFragment = FontsFragment()
            }
            Globals.NOTIFICATION_HOME -> {
                baseFragment = NotificationHomeFragment()
            }
            Globals.NOTIFICATION -> {
                baseFragment = NotificationFragment()
            }
            Globals.MACHINE_LEARNING -> {
                baseFragment = MachineLearningFragment()
            }
            Globals.TEXT_RECOGNITION -> {
                baseFragment = TextRecognitionFragment()
            }
            Globals.FACIAL_DETECTION -> {
                baseFragment = FacialDetectionFragment()
            }
            Globals.EXOPLAYER -> {
                baseFragment = ExoPlayerFragment()
            }
            Globals.PUNTONET -> {
                baseFragment = PuntoNetFragment()
            }
            Globals.MVVM_USER_INPUT -> {
                baseFragment = UserInputFragment()
            }
            Globals.MVVM_LOGIN_RESULT -> {
                baseFragment = LoginResultFragment()
            }
            Globals.MVVM_LOGIN -> {
                baseFragment = LoginInputFragment()
            }
            Globals.MVVM_USERS -> {
                baseFragment = MvvmUsersFragment()
            }
            Globals.COROUTINE_HOME -> {
                baseFragment = CoroutineHomeFragment()
            }
            Globals.COROUTINE_BASICS -> {
                baseFragment = CoroutineBasicsFragment()
            }
            Globals.COROUTINE_SCOPES -> {
                baseFragment = CoroutineScopesFragment()
            }
            Globals.COROUTINE_JOBS_CANCELLATION -> {
                baseFragment = CoroutineJobsCancellationFragment()
            }
            Globals.COROUTINE_RUN_BLOCKING -> {
                baseFragment = CoroutineRunBlockingFragment()
            }
            Globals.COROUTINE_VALUES -> {
                baseFragment = CoroutineValuesFragment()
            }
            Globals.COROUTINE_CHANNELS -> {
                baseFragment = CoroutineChannelsFragment()
            }
            Globals.PAGING -> {
                baseFragment = RickMortyFragment()
            }
            Globals.USERS_HOME -> {
                baseFragment = UsersHomeFragment()
            }
            Globals.USERS -> {
                baseFragment = UsersFragment()
            }
            Globals.USERS_PAGING -> {
                baseFragment = UsersPagingFragment()
            }
            Globals.USERS_DETAIL -> {
                baseFragment = UsersDetailFragment()
            }
            Globals.RX_HOME -> {
                baseFragment = RxHomeFragment()
            }
            Globals.RX_EXAMPLE1 -> {
                baseFragment = RxExample1Fragment()
            }
            Globals.RX_EXAMPLE2 -> {
                baseFragment = RxExample2Fragment()
            }
            Globals.RX_EXAMPLE3 -> {
                baseFragment = RxExample3Fragment()
            }
            Globals.RX_EXAMPLE4 -> {
                baseFragment = RxExample4Fragment()
            }
            Globals.RX_RETROFIT -> {
                baseFragment = RxRetrofitFragment()
            }
            Globals.DEPENDENCY_INJECTION -> {
                baseFragment = DependencyInjectionFragment()
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
            .add(R.id.frame_container, DialogFlowFragment(), mDialogFlowFragment)
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
            baseFragmentView.targetFragment?.onActivityResult(
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
            baseFragmentView.targetFragment?.onActivityResult(
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
        hideProgressDialog()

        // check if is resumed by a push
        if (pushBundle != null) {
            /*
            if (pushBundle?.containsKey(EmployeeFirebaseMessagingService.KEY_CAMPAIGN_ID)) {
                openDetail(Globals.FRAGMENT_GREEN_GRASS_DETAIL, pushBundle, mainFragment, Globals.REQUEST_CODE_GREEN_GRASS_DETAIL)
            }
            */
            pushBundle = null
        } else {
            if (UserFactory.getInstance().isLogged) {
                // manage deeplink
                if (deepLinkEvent != null && deepLinkEvent?.deeplink != null) {
                    openDeepLink(deepLinkEvent?.deeplink!!)
                    clearDeepLinkEvent()
                }
            }
        }
    }

    private fun clearDeepLinkEvent() {
        deepLinkEvent = null
    }

    fun openApp(packageName: String) {
        Utils.openApp(this, packageName)
    }

    fun openBrowser(url: String) {
        Utils.openBrowser(this, url)
    }

    override fun onDestroy() {
        super.onDestroy()
        layoutBinding = null
        handler1?.removeCallbacks(m1Runnable)
        handler2?.removeCallbacks(m2Runnable)
        handler3?.removeCallbacks(m3Runnable)
        handler4?.removeCallbacks(m4Runnable)
        handler5?.removeCallbacks(m5Runnable)
        handler6?.removeCallbacks(m6Runnable)
    }
}