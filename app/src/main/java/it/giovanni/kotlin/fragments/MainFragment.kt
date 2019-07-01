package it.giovanni.kotlin.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import it.giovanni.kotlin.adapters.HomeFragmentAdapter
import it.giovanni.kotlin.interfaces.IDataRefresh
import it.giovanni.kotlin.utils.Globals
import it.giovanni.kotlin.BuildConfig
import it.giovanni.kotlin.R
import it.giovanni.kotlin.activities.MainActivity
import it.giovanni.kotlin.bean.LinkMenu
import it.giovanni.kotlin.utils.UserFactory
import it.giovanni.kotlin.utils.Utils
import kotlinx.android.synthetic.main.main_content_layout.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainFragment : BaseFragment(SectionType.MAIN) {

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
    private lateinit var pagesAdapter: HomeFragmentAdapter
    private val END_SCALE = 0.9f
    private var listaLinkMenu: ArrayList<LinkMenu>? = null
    private val WEBVIEW_TYPE = "webview"
    private val APPLINK_TYPE = "inAppLink"
    private val APP_TYPE = "app"
    private val EXT_TYPE = "ext"
    private var bundleDeepLink: Bundle = Bundle()

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

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
        val roundAvatar : Bitmap = Utils.getRoundBitmap(avatar, avatar.width)
        nav_header_avatar.setImageBitmap(roundAvatar)
    }

    private fun draw() {

        val displayMetrics = DisplayMetrics()
        currentActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        listaLinkMenu =
            if (UserFactory.getInstance().listaLinkMenu != null)
                UserFactory.getInstance().listaLinkMenu!!
            else init()

        attachViewPager()
        attachRightMenu(listaLinkMenu)
        attachTabListener()
        resetBackgroundTabNav()
    }

    private fun attachViewPager() {
        pagesAdapter = HomeFragmentAdapter(childFragmentManager, 3, this)

        view_pager.adapter = pagesAdapter
        view_pager.offscreenPageLimit = 3
        view_pager.currentItem = (TAB_INDEX_HOME - 1)

        view_pager.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    translateAnimation(position + 1, getTabInstanceByPosition(position + 1),
                        getDrawableTabInstanceByPosition(position + 1, false),
                        getDrawableTabInstanceByPosition(position + 1, true))

                    when (position) {
                        0 -> {
                            rooms_available.visibility = View.GONE
                            currentPosition = 0
                        }
                        1 -> {
                            rooms_available.visibility = View.VISIBLE
                            currentPosition = 1
                        }
                        2 -> {
                            rooms_available.visibility = View.GONE
                            currentPosition = 2
                        }
                    }
                }
            }
        )
    }

    private fun attachRightMenu(listaLinkMenu: ArrayList<LinkMenu>?) {
        drawer_layout.isEnabled = false
        drawer_layout.setScrimColor(Color.TRANSPARENT)

        drawer_layout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                // Scale the View based on current slide offset
                val diffScaledOffset = slideOffset * (1 - END_SCALE)
                val offsetScale = 1 - diffScaledOffset
                container.scaleX = offsetScale
                container.scaleY = offsetScale

                // Translate the View, accounting for the scaled width
                val xOffset = drawerView.width * slideOffset
                val xOffsetDiff = container.width * diffScaledOffset / 2
                val xTranslation = xOffset - xOffsetDiff
                container.translationX = - xTranslation
                currentActivity.window.statusBarColor = ContextCompat.getColor(context!!, R.color.dark)
            }

            override fun onDrawerClosed(drawerView: View) {
                currentActivity.window.statusBarColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
                tab_menu.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_menu))
            }
        })

        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        logout_container.setOnClickListener {
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

        if (listaLinkMenu != null) {
            addViews(listaLinkMenu)
        }
    }

    private fun addViews(listaLinkMenu: ArrayList<LinkMenu>?) {
        if (listaLinkMenu == null)
            return
        if (listaLinkMenu.size == 0)
            return
        for (item in listaLinkMenu) {

            val rowView = LayoutInflater.from(context).inflate(
                R.layout.nav_header_item,
                nav_header_container,
                false)
            val labelName: TextView = rowView.findViewById(R.id.label_name)
            labelName.text = item.name

            nav_header_container.addView(rowView)

            labelName.setOnClickListener {
                closeRightMenu()
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
        tab_home.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_home_rvd))
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
                    ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_administrative_area)!!
                else
                    ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_administrative_area_rvd)!!
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
        openRightMenu()
    }

    private fun closeRightMenu() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
            tab_menu.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_menu))
        }
    }

    private fun openRightMenu() {
        drawer_layout.isEnabled = true
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
            tab_menu.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_menu))
        } else {
            drawer_layout.openDrawer(GravityCompat.END)
            tab_menu.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ico_close_menu))
        }
    }

    private fun translateAnimation(tab: Int, imageView: ImageView, drawable_from: Drawable, drawable_to: Drawable) {
        if (animationFinish) {
            resetAllImages()
            val offset: Float = background_bottom_bar.x + ((tab - previousTab) * defaultX)
            previousTab = tab
            val valueAnimator: ValueAnimator = ObjectAnimator.ofFloat(background_bottom_bar, "translationX", offset).apply {
                duration = TRANSITION_TIME
            }

            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // done
                    animationFinish = true
                    // check in right menu is open
                    closeRightMenu()
                }
            })

            animationFinish = false
            valueAnimator.start()
            tabSelectedDrawableAnimation(imageView, drawable_from, drawable_to)
        }
    }

    private fun tabSelectedDrawableAnimation(imageView: ImageView, drawable_from: Drawable, drawable_to: Drawable) {

        // set res image array
        val transitionDrawable = TransitionDrawable(arrayOf(drawable_from, drawable_to))

        // your image view here - backgroundImageView
        imageView.setImageDrawable(transitionDrawable)
        transitionDrawable.startTransition(TRANSITION_TIME.toInt())
        transitionDrawable.isCrossFadeEnabled = false // call public methods
    }

    private fun resetAllImages() {
        tab_home.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_home))
        tab_working_area.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_working_area))
        tab_administrative.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ico_bottom_administrative_area))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Globals.REQUEST_CODE_EVENT_WORKING_AREA) {
            if (data != null) {
                (pagesAdapter.getItem(1) as IDataRefresh).refresh()
            }
        } else if (requestCode == Globals.REQUEST_CODE_EVENT_HOME) {
            if (data != null) {
                (pagesAdapter.getItem(1) as IDataRefresh).refresh()
            }
        }
    }

    private fun init(): ArrayList<LinkMenu> {

        val list = ArrayList<LinkMenu>()
        list.add(LinkMenu("Wind_Tre_Per_Noi", "WindTre Per Noi", "https:\\/\\/eudaimonint.secure.force.com\\/sso\\/SSOAuthenticationPage?azienda=wind&user=150511&hash=8780a3af7b6d86bb366a3e21cedef465", "1", "webview", ""))
        list.add(LinkMenu("Intranet", "Intranet", "https:\\/\\/intranet3.sharepoint.com\\/Pages\\/Home.aspx", "2", "webview", ""))
        list.add(LinkMenu("Bacheca", "Bacheca", "https:\\/\\/intranet3.sharepoint.com\\/sites\\/bacheca\\/Pagine\\/Bacheca.aspx", "3", "webview", ""))
        list.add(LinkMenu("Emergenze", "Emergenze", "https:\\/\\/intranet3.sharepoint.com\\/Pages\\/Utilities_Servizi\\/AmbienteSicurezza.aspx", "4", "webview", ""))
        list.add(LinkMenu("Fondo_Solidarietà", "Fondo Solidarietà", "https:\\/\\/sisalute.gruppofos.com\\/Login\\/", "5", "webview", ""))
        list.add(LinkMenu("Offerta_Dipendenti", "Offerta Dipendenti", "https:\\/\\/intranet3.sharepoint.com\\/sites\\/WindTrePerNoi\\/Pagine\\/Offerta-dipendenti.aspx", "6", "webview", ""))
        list.add(LinkMenu("#Time4Me", "#Time4Me", "https:\\/\\/intranet3.sharepoint.com\\/Pages\\/Time4Me\\/pages\\/Home.aspx", "7", "webview", ""))
        list.add(LinkMenu("CCS", "CCS", "", "8", "app", "it.ccsitalia.app"))
        list.add(LinkMenu("Password_Manager", "Password Manager", "https:\\/\\/myaccount.windtre.it\\/", "9", "webview", ""))

        return list
    }
}