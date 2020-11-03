@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import it.giovanni.arkivio.App
import it.giovanni.arkivio.BuildConfig
import it.giovanni.arkivio.R
import it.giovanni.arkivio.activities.MainActivity
import it.giovanni.arkivio.adapters.HomeFragmentAdapter
import it.giovanni.arkivio.bean.Link
import it.giovanni.arkivio.bean.LinkSide
import it.giovanni.arkivio.customview.popup.CustomDialogPopup
import it.giovanni.arkivio.databinding.MainLayoutBinding
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.UserFactory
import it.giovanni.arkivio.utils.Utils.Companion.getRoundBitmap
import it.giovanni.arkivio.utils.Utils.Companion.setBitmapFromUrl
import it.giovanni.arkivio.viewinterfaces.IDarkMode
import it.giovanni.arkivio.viewinterfaces.IDataRefresh
import kotlinx.android.synthetic.main.link_area_layout.*
import kotlinx.android.synthetic.main.main_content_layout.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainFragment : BaseFragment(SectionType.MAIN), IDarkMode.View {

    companion object {
        var TAB_INDEX_HOME: Int = 1
        var TAB_INDEX_WORKING_AREA: Int = 2
        var TAB_INDEX_ADMINISTRATIVE: Int = 3
    }

    private var currentPosition: Int = 0
    private var defaultX: Float = 0f
    private var TRANSITION_TIME: Long = 100
    var animationFinish = true
    private var previousTab: Int = TAB_INDEX_HOME
    private lateinit var fragmentAdapter: HomeFragmentAdapter
    private val END_SCALE = 0.9f
    private var listLink: ArrayList<Link>? = null
    private var listLinkSide: ArrayList<LinkSide>? = null
    private val WEBVIEW_TYPE = "webview"
    private val APPLINK_TYPE = "inAppLink"
    private val APP_TYPE = "app"
    private val EXT_TYPE = "ext"
    private var bundleDeepLink: Bundle = Bundle()

    private lateinit var customPopup: CustomDialogPopup
    private var compress: Boolean = false

    // DATA BINDING
    private var binding: MainLayoutBinding? = null
    private var presenter: DarkModePresenter? = null
    private var model: DarkModeModel? = null

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = super.onCreateView(inflater, container, savedInstanceState)

        // ----- DATA BINDING ----- //
        binding = DataBindingUtil.inflate(inflater, R.layout.main_layout, container, false)
        view = binding?.root
        presenter = DarkModePresenter(this, context!!)
        model = DarkModeModel(context!!)
        binding?.temp = model
        binding?.presenter = presenter
        // ------------------------ //

        setStatusBarColor()

        hideProgressDialog() // TODO: Questo l'ho messo io qui.

        val drawListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                draw()
            }
        }

        view!!.viewTreeObserver.addOnGlobalLayoutListener(drawListener)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val avatar : Bitmap = BitmapFactory.decodeResource(context!!.resources, R.drawable.giovanni)
        val roundAvatar : Bitmap = getRoundBitmap(avatar, avatar.width)
        nav_header_avatar.setImageBitmap(roundAvatar)

        switch_mode.isChecked = !isDarkMode
        switch_mode.setOnClickListener {
            onSetLayout(model)
        }
    }

    private fun draw() {

        val displayMetrics = DisplayMetrics()
        currentActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        listLink =
            if (UserFactory.getInstance().listLink != null)
                UserFactory.getInstance().listLink
            else init()

        listLinkSide =
            if (UserFactory.getInstance().listLinkSide != null)
                UserFactory.getInstance().listLinkSide
            else initSide()

        attachViewPager()
        attachSideMenu(listLinkSide)
        attachTabListener()
        resetBackgroundTabNav()

        if (listLink != null) {
            addLinkViews(listLink)
        }

        voice.setOnClickListener {
            currentActivity.openDialogDetail(Globals.DIALOG_FLOW, Bundle())
            currentActivity.window.statusBarColor = ContextCompat.getColor(
                context!!,
                R.color.black_transparent_1
            )
        }
    }

    fun goToHomePosition(position: Int) {
        view_pager.currentItem = position - 1
    }

    private fun attachViewPager() {
        fragmentAdapter = HomeFragmentAdapter(childFragmentManager, 3, this)

        view_pager.adapter = fragmentAdapter
        view_pager.offscreenPageLimit = 3
        view_pager.currentItem = (TAB_INDEX_HOME - 1)

        view_pager.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    translateAnimation(
                        position + 1, getTabInstanceByPosition(position + 1),
                        getDrawableTabInstanceByPosition(position + 1, false),
                        getDrawableTabInstanceByPosition(position + 1, true)
                    )

                    when (position) {
                        0 -> {
                            voice.visibility = View.VISIBLE
                            currentPosition = 0
                        }
                        1 -> {
                            voice.visibility = View.GONE
                            currentPosition = 1
                        }
                        2 -> {
                            voice.visibility = View.GONE
                            currentPosition = 2
                        }
                    }
                }
            }
        )
    }

    private fun attachSideMenu(listLinkSide: ArrayList<LinkSide>?) {
        drawer_layout.isEnabled = false
        drawer_layout.setScrimColor(Color.TRANSPARENT)

        drawer_layout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                if (currentActivity.preferences.getBoolean("COMPRESS", false)) {
                    // Scale the View based on current slide offset
                    val diffScaledOffset = slideOffset * (1 - END_SCALE)
                    val offsetScale = 1 - diffScaledOffset
                    container.scaleX = offsetScale
                    container.scaleY = offsetScale

                    // Translate the View, accounting for the scaled width
                    val xOffset = drawerView.width * slideOffset
                    val xOffsetDiff = container.width * diffScaledOffset / 2
                    val xTranslation = xOffset - xOffsetDiff
                    container.translationX = -xTranslation
                } else {
                    // Scale the View based on current slide offset
                    val diffScaledOffset = slideOffset * (1 - END_SCALE)
                    val offsetScale = 1.0f
                    container.scaleX = offsetScale
                    container.scaleY = offsetScale

                    // Translate the View, accounting for the scaled width
                    val xOffset = drawerView.width * slideOffset
                    val xOffsetDiff = container.width * diffScaledOffset / 10
                    val xTranslation = xOffset - xOffsetDiff
                    container.translationX = -xTranslation
                }
                currentActivity.window.statusBarColor = ContextCompat.getColor(
                    context!!,
                    R.color.blueWave
                )
            }

            override fun onDrawerClosed(drawerView: View) {
                setStatusBarColor()
                tab_menu.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ico_bottom_menu
                    )
                )
            }
        })

        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        settings.setOnClickListener {
            customPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
            customPopup.setCancelable(false)
            customPopup.setTitle("IMPOSTAZIONI", "")
            customPopup.setMessage("Vuoi comprimere il background all'apertura del menu laterale?")

            customPopup.setButtons(
                resources.getString(R.string.button_yes), {
                    compress = true
                    saveStateToPreferences()
                    customPopup.dismiss()
                },
                resources.getString(R.string.button_no), {
                    compress = false
                    saveStateToPreferences()
                    customPopup.dismiss()
                }
            )
            customPopup.show()
        }

        logout.setOnClickListener {
            (activity as MainActivity).logout()
        }

        if (BuildConfig.CREDITS_PREFIX == "DEBUG") {
            val versionName = BuildConfig.VERSION_NAME + " " + BuildConfig.CREDITS_PREFIX
            version_name.text = versionName
        } else {
            val versionName = BuildConfig.VERSION_NAME
            version_name.text = versionName
        }

        val versionCode = "(" + BuildConfig.VERSION_CODE.toString() + ")"
        version_code.text = versionCode

        if (listLinkSide != null) {
            addSideViews(listLinkSide)
        }
    }

    private fun addLinkViews(listLink: ArrayList<Link>?) {
        if (listLink == null)
            return
        if (listLink.size == 0)
            return
        for (item in listLink) {

            val rowView = LayoutInflater.from(context).inflate(
                R.layout.link_dynamic_item,
                link_utili_container,
                false
            )

            val labelName: TextView = rowView.findViewById(R.id.link_name)
            val labelIcon: ImageView = rowView.findViewById(R.id.link_icon)

            setBitmapFromUrl(item.image, labelIcon, activity!!)

            val linkItem: RelativeLayout = rowView.findViewById(R.id.link_item)

            labelName.text = item.name

            link_utili_container.addView(rowView)

            linkItem.setOnClickListener {
                when (item.type) {
                    WEBVIEW_TYPE -> {
                        bundleDeepLink.putString("link_deeplink", item.name)
                        bundleDeepLink.putString("url_deeplink", item.link)
                        currentActivity.openDetail(Globals.WEB_VIEW, bundleDeepLink)
                    }
                    APPLINK_TYPE -> {
                        if (item.link == "waw3://cinema") {
                            // gAnalytics.sendEvent(Mapping.GAnalyticsKey.CATEGORY_EXTERNAL_LINK, "Click", "GrandeCinema3", null)
                            getGPSCoordinates()
                        } else if (item.link == "waw3://contacts") {
                            currentActivity.openDetail(Globals.RUBRICA_HOME, null)
                        }
                    }
                    APP_TYPE -> {
                        // val params = Bundle()
                        // params.putString(Mapping.WAW3Key.WAW3_LINK, item.analyticsLabel)
                        // trackEvent(params)
                        currentActivity.openApp(item.appLinkAndroid)
                    }
                    EXT_TYPE -> {
                        currentActivity.openBrowser(item.link)
                    }
                }
            }
        }
        link_utili_container.visibility = View.VISIBLE
    }

    private fun addSideViews(listLinkSide: ArrayList<LinkSide>?) {
        if (listLinkSide == null)
            return
        if (listLinkSide.size == 0)
            return
        for (item in listLinkSide) {

            val rowView = LayoutInflater.from(context).inflate(
                R.layout.nav_header_item,
                nav_header_container,
                false
            )

            val labelName: TextView = rowView.findViewById(R.id.label_name)
            labelName.text = item.name

            nav_header_container.addView(rowView)

            labelName.setOnClickListener {
                closeSideMenu()
                when (item.type) {
                    WEBVIEW_TYPE -> {
                        bundleDeepLink.putString("link_deeplink", item.name)
                        bundleDeepLink.putString("url_deeplink", item.link)
                        currentActivity.openDetail(Globals.WEB_VIEW, bundleDeepLink)
                    }
                    APPLINK_TYPE -> {
                        currentActivity.openDetail(Globals.RUBRICA_HOME, null)
                    }
                    APP_TYPE -> {
                        currentActivity.openApp(item.appLinkAndroid)
                    }

                    EXT_TYPE -> {
                        currentActivity.openBrowser(item.link)
                    }
                }
            }
        }
        nav_header_container.visibility = View.VISIBLE
    }

    private fun attachTabListener() {
        tab_home.setOnClickListener(tabHomeClick)
        tab_working_area.setOnClickListener(tabWorkingAreaClick)
        tab_administrative.setOnClickListener(tabAdministrativeClick)
        tab_menu.setOnClickListener(tabMenuClick)
    }

    private fun resetBackgroundTabNav() {
        val start = tab_home.x + (tab_home.width / 2)
        val end = tab_menu.x + (tab_menu.width / 2)
        val delta = (end - start) / 4

        background_bottom_bar.x = -(delta * 2)
        tab_home.setImageDrawable(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ico_bottom_home_rvd
            )
        )
        // tab_home.setColorFilter(ContextCompat.getColor(context!!, R.color.blueWave), android.graphics.PorterDuff.Mode.SRC_IN)
        defaultX = (tab_home.width).toFloat()
    }

    private fun getTabInstanceByPosition(position: Int): ImageView {
        return when (position) {
            1 -> tab_home
            2 -> tab_working_area
            3 -> tab_administrative
            else -> tab_home
        }
    }

    private fun getDrawableTabInstanceByPosition(position: Int, rvdType: Boolean): Drawable {
        when (position) {
            1 -> {
                return if (!rvdType)
                    ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_home)!!
                else
                    ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_home_rvd)!!
            }
            2 -> {
                return if (!rvdType)
                    ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_working_area)!!
                else
                    ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_working_area_rvd)!!
            }
            3 -> {
                return if (!rvdType)
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ico_bottom_administrative_area
                    )!!
                else
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ico_bottom_administrative_area_rvd
                    )!!
            }
            else -> {
                return if (!rvdType)
                    ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_home)!!
                else
                    ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_home_rvd)!!
            }
        }
    }

    private var tabHomeClick: View.OnClickListener = View.OnClickListener {
        translateAnimation(
            TAB_INDEX_HOME,
            tab_home,
            ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_home)!!,
            ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_home_rvd)!!
        )
        view_pager.currentItem = TAB_INDEX_HOME - 1
    }

    private var tabWorkingAreaClick: View.OnClickListener = View.OnClickListener {
        translateAnimation(
            TAB_INDEX_WORKING_AREA,
            tab_working_area,
            ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_working_area)!!,
            ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_working_area_rvd)!!
        )
        view_pager.currentItem = TAB_INDEX_WORKING_AREA - 1
    }

    private var tabAdministrativeClick: View.OnClickListener = View.OnClickListener {
        translateAnimation(
            TAB_INDEX_ADMINISTRATIVE,
            tab_administrative,
            ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_administrative_area)!!,
            ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_administrative_area_rvd)!!
        )
        view_pager.currentItem = TAB_INDEX_ADMINISTRATIVE - 1
    }

    private var tabMenuClick: View.OnClickListener = View.OnClickListener {
        openSideMenu()
    }

    private fun closeSideMenu() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
            tab_menu.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ico_bottom_menu
                )
            )
        }
    }

    private fun openSideMenu() {
        drawer_layout.isEnabled = true
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
            tab_menu.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ico_bottom_menu
                )
            )
        } else {
            drawer_layout.openDrawer(GravityCompat.END)
            tab_menu.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ico_close_menu
                )
            )
        }
    }

    private fun translateAnimation(
        tab: Int,
        imageView: ImageView,
        drawable_from: Drawable,
        drawable_to: Drawable
    ) {
        if (animationFinish) {
            resetAllImages()
            val offset: Float = background_bottom_bar.x + ((tab - previousTab) * defaultX)
            previousTab = tab
            val valueAnimator: ValueAnimator = ObjectAnimator.ofFloat(
                background_bottom_bar,
                "translationX",
                offset
            ).apply {
                duration = TRANSITION_TIME
            }

            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animationFinish = true
                    // check in side menu is open
                    closeSideMenu()
                }
            })

            animationFinish = false
            valueAnimator.start()
            tabSelectedDrawableAnimation(imageView, drawable_from, drawable_to)
        }
    }

    private fun tabSelectedDrawableAnimation(
        imageView: ImageView,
        drawable_from: Drawable,
        drawable_to: Drawable
    ) {

        // set res image array
        val transitionDrawable = TransitionDrawable(arrayOf(drawable_from, drawable_to))

        // your image view here - backgroundImageView
        imageView.setImageDrawable(transitionDrawable)
        transitionDrawable.startTransition(TRANSITION_TIME.toInt())
        transitionDrawable.isCrossFadeEnabled = false // call public methods
    }

    private fun resetAllImages() {
        tab_home.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_home))
        tab_working_area.setImageDrawable(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ico_bottom_working_area
            )
        )
        tab_administrative.setImageDrawable(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ico_bottom_administrative_area
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Globals.REQUEST_CODE_EVENT_WORKING_AREA) {
            if (data != null) {
                (fragmentAdapter.getItem(1) as IDataRefresh).refresh()
            }
        } else if (requestCode == Globals.REQUEST_CODE_EVENT_HOME) {
            if (data != null) {
                (fragmentAdapter.getItem(1) as IDataRefresh).refresh()
            }
        }
    }

    private fun setStatusBarColor() {
        // currentActivity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        currentActivity.window.statusBarColor = ContextCompat.getColor(
            App.context,
            android.R.color.transparent
        )
        // currentActivity.window.navigationBarColor = currentActivity.resources.getColor(android.R.color.transparent)
        currentActivity.window.setBackgroundDrawable(
            ContextCompat.getDrawable(
                App.context,
                R.drawable.background_empty
            )
        )
    }

    private fun getGPSCoordinates() {
        showProgressDialog()
        currentActivity.requestGPSPermission()
    }

    private fun init(): ArrayList<Link> {

        val list = ArrayList<Link>()
        list.add(
            Link(
                "",
                "Grande Cinema 3",
                "waw3://cinema",
                "1",
                "inAppLink",
                "",
                "https://static.windtrebusiness.it/mosaicow3b/static/configuration/ico_gctre.png"
            )
        )
        list.add(
            Link(
                "Daytronic",
                "DayTronic",
                "",
                "2",
                "app",
                "it.day.daytronicFLAT",
                "https://static.windtrebusiness.it/mosaicow3b/static/configuration/ico_daytronic.png"
            )
        )
        list.add(Link("", "Gympass", "", "3", "app", "com.gympass", ""))
        list.add(
            Link(
                "Bacheca",
                "Rubrica",
                "waw3://contacts",
                "4",
                "inAppLink",
                "",
                "https://static.windtrebusiness.it/mosaicow3b/static/configuration/ico_rubrica.png"
            )
        )
        return list
    }

    private fun initSide(): ArrayList<LinkSide> {

        val list = ArrayList<LinkSide>()
        list.add(
            LinkSide(
                "Wind_Tre_Per_Noi",
                "WINDTRE Per Noi",
                "https://eudaimonint.secure.force.com/sso/SSOAuthenticationPage?azienda=wind&user=150511&hash=8780a3af7b6d86bb366a3e21cedef465",
                "1",
                "webview",
                ""
            )
        )
        list.add(
            LinkSide(
                "Intranet",
                "Intranet",
                "https://intranet3.sharepoint.com/Pages/Home.aspx",
                "2",
                "webview",
                ""
            )
        )
        list.add(
            LinkSide(
                "Bacheca",
                "Bacheca",
                "https://intranet3.sharepoint.com/sites/bacheca/Pagine/Bacheca.aspx",
                "3",
                "webview",
                ""
            )
        )
        list.add(
            LinkSide(
                "Emergenze",
                "Emergenze",
                "https://intranet3.sharepoint.com/Pages/Utilities_Servizi/AmbienteSicurezza.aspx",
                "4",
                "webview",
                ""
            )
        )
        list.add(
            LinkSide(
                "Fondo_Solidarietà",
                "Fondo Solidarietà",
                "https://sisalute.gruppofos.com/Login/",
                "5",
                "webview",
                ""
            )
        )
        list.add(
            LinkSide(
                "Offerta_Dipendenti",
                "Offerta Dipendenti",
                "https://intranet3.sharepoint.com/sites/WindTrePerNoi/Pagine/Offerta-dipendenti.aspx",
                "6",
                "webview",
                ""
            )
        )
        list.add(
            LinkSide(
                "#Time4Me",
                "#Time4Me",
                "https://intranet3.sharepoint.com/Pages/Time4Me/pages/Home.aspx",
                "7",
                "webview",
                ""
            )
        )
        list.add(LinkSide("CCS", "CCS", "", "8", "app", "it.ccsitalia.app"))
        list.add(
            LinkSide(
                "Password_Manager",
                "Password Manager",
                "https://myaccount.windtre.it/",
                "9",
                "webview",
                ""
            )
        )
        return list
    }

    override fun onShowDataModel(model: DarkModeModel?) {}

    override fun onSetLayout(model: DarkModeModel?) {

        var mModel: DarkModeModel? = model
        isDarkMode = !isDarkMode
        saveStateToPreferences(isDarkMode)

        mModel = DarkModeModel(context!!)
        binding?.temp = mModel
        binding?.presenter = presenter

        /*
        if (isDarkMode)
            currentActivity.setTheme(R.style.AppThemeDark)
        else
            currentActivity.setTheme(R.style.AppTheme)
        */
    }

    private fun saveStateToPreferences(isDarkMode: Boolean) {
        val editor = currentActivity.preferences.edit()
        editor.putBoolean("DARK_MODE", isDarkMode)
        editor.apply()
    }

    private fun saveStateToPreferences() {
        val editor: SharedPreferences.Editor = currentActivity.preferences.edit()
        editor.putBoolean("COMPRESS", compress)
        editor.apply()
    }
}