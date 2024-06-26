package it.giovanni.arkivio.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import it.giovanni.arkivio.BuildConfig
import it.giovanni.arkivio.R
import it.giovanni.arkivio.activities.MainActivity
import it.giovanni.arkivio.adapters.HomeFragmentAdapter
import it.giovanni.arkivio.model.Link
import it.giovanni.arkivio.model.LinkSide
import it.giovanni.arkivio.customview.popup.CustomDialogPopup
import it.giovanni.arkivio.databinding.LinkDynamicItemBinding
import it.giovanni.arkivio.databinding.MainLayoutBinding
import it.giovanni.arkivio.databinding.NavHeaderItemBinding
import it.giovanni.arkivio.fragments.homepage.LinkAreaFragment.Companion.linkAreaLayoutBinding
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.SharedPreferencesManager.loadCompressStateFromPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.saveCompressStateToPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.saveDarkModeStateToPreferences
import it.giovanni.arkivio.utils.UserFactory
import it.giovanni.arkivio.utils.Utils.getRoundBitmap
import it.giovanni.arkivio.utils.Utils.setBitmapFromUrl
import it.giovanni.arkivio.viewinterfaces.IDarkMode

class MainFragment : BaseFragment(SectionType.MAIN), IDarkMode.View {

    companion object {
        var TAB_INDEX_HOME: Int = 1
        var TAB_INDEX_WORKING_AREA: Int = 2
        var TAB_INDEX_LINK_AREA: Int = 3
    }

    private var layoutBinding: MainLayoutBinding? = null
    private val binding get() = layoutBinding
    private var darkModePresenter: DarkModePresenter? = null
    private var model: DarkModeModel? = null

    private var defaultX: Float = 0f
    private var transitionTime: Long = 100
    var animationFinish = true
    private var previousTab: Int = TAB_INDEX_HOME
    private lateinit var fragmentAdapter: HomeFragmentAdapter
    private val endScale = 0.9f
    private var listLink: ArrayList<Link>? = null
    private var listLinkSide: ArrayList<LinkSide>? = null
    private val webviewType = "webview"
    private val appLinkType = "inAppLink"
    private val appType = "app"
    private val extType = "ext"
    private var bundleDeepLink: Bundle = Bundle()

    private lateinit var customDialogPopup: CustomDialogPopup
    private var compress: Boolean = false

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        var view = super.onCreateView(inflater, container, savedInstanceState) // Non rimuovere.

        layoutBinding = MainLayoutBinding.inflate(inflater, container, false)

        view = binding?.root

        darkModePresenter = DarkModePresenter(this)
        model = DarkModeModel(requireContext())
        binding?.temp = model
        binding?.presenter = darkModePresenter

        currentActivity.setDarkModeStatusBarTransparent()

        hideProgressDialog()

        val drawListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                draw()
            }
        }

        view?.viewTreeObserver?.addOnGlobalLayoutListener(drawListener)!!

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val avatar : Bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.giovanni)
        val roundAvatar : Bitmap = getRoundBitmap(avatar, avatar.width)
        binding?.navHeader?.navHeaderAvatar?.setImageBitmap(roundAvatar)

        binding?.navHeader?.switchMode?.isChecked = !isDarkMode

        binding?.navHeader?.switchMode?.setOnClickListener {

            val customPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
            customPopup.setCancelable(false)
            customPopup.setTitle("", "")
            customPopup.setMessage(resources.getString(R.string.switch_mode_message))

            customPopup.setButtons(
                resources.getString(R.string.button_confirm),
                {
                    customPopup.dismiss()
                    onSetLayout(model)
                    (activity as MainActivity).logout()
                },
                resources.getString(R.string.button_cancel),
                {
                    customPopup.dismiss()
                    binding?.navHeader?.switchMode?.isChecked = !isDarkMode
                }
            )
            customPopup.show()
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

        binding?.mainContent?.voice?.setOnClickListener {
            currentActivity.openDialogDetail(Globals.DIALOG_FLOW, Bundle())
            currentActivity.window.statusBarColor = ContextCompat.getColor(
                requireContext(),
                R.color.black_transparent_1
            )
        }
    }

    fun goToHomePosition(position: Int) {
        binding?.mainContent?.viewPager?.currentItem = position - 1
    }

    private fun attachViewPager() {

        fragmentAdapter = HomeFragmentAdapter(childFragmentManager, 3, this)

        binding?.mainContent?.viewPager?.adapter = fragmentAdapter
        binding?.mainContent?.viewPager?.offscreenPageLimit = 3
        binding?.mainContent?.viewPager?.currentItem = (TAB_INDEX_HOME - 1)

        binding?.mainContent?.viewPager?.addOnPageChangeListener(
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
                            binding?.mainContent?.voice?.visibility = View.VISIBLE
                        }
                        1 -> {
                            binding?.mainContent?.voice?.visibility = View.GONE
                        }
                        2 -> {
                            binding?.mainContent?.voice?.visibility = View.GONE
                        }
                    }
                }
            }
        )
    }

    private fun attachSideMenu(listLinkSide: ArrayList<LinkSide>?) {
        binding?.drawerLayout?.isEnabled = false
        binding?.drawerLayout?.setScrimColor(Color.TRANSPARENT)

        binding?.drawerLayout?.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                if (loadCompressStateFromPreferences()) {
                    // Scale the View based on current slide offset
                    val diffScaledOffset = slideOffset * (1 - endScale)
                    val offsetScale = 1 - diffScaledOffset
                    binding?.mainContent?.container?.scaleX = offsetScale
                    binding?.mainContent?.container?.scaleY = offsetScale

                    // Translate the View, accounting for the scaled width
                    val xOffset = drawerView.width * slideOffset
                    val xOffsetDiff = binding?.mainContent?.container?.width!! * diffScaledOffset / 2
                    val xTranslation = xOffset - xOffsetDiff
                    binding?.mainContent?.container?.translationX = -xTranslation
                } else {
                    // Scale the View based on current slide offset
                    val diffScaledOffset = slideOffset * (1 - endScale)
                    val offsetScale = 1.0f
                    binding?.mainContent?.container?.scaleX = offsetScale
                    binding?.mainContent?.container?.scaleY = offsetScale

                    // Translate the View, accounting for the scaled width
                    val xOffset = drawerView.width * slideOffset
                    val xOffsetDiff = binding?.mainContent?.container?.width!! * diffScaledOffset / 10
                    val xTranslation = xOffset - xOffsetDiff
                    binding?.mainContent?.container?.translationX = -xTranslation
                }
            }

            override fun onDrawerClosed(drawerView: View) {
                binding?.mainContent?.tabMenu?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ico_bottom_menu
                    )
                )
            }
        })

        binding?.drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        binding?.navHeader?.settings?.setOnClickListener {
            customDialogPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
            customDialogPopup.setCancelable(false)
            customDialogPopup.setTitle(context?.resources?.getString(R.string.settings)!!, "")
            customDialogPopup.setMessage(context?.resources?.getString(R.string.settings_message)!!)

            customDialogPopup.setButtons(
                resources.getString(R.string.button_yes), {
                    compress = true
                    saveCompressStateToPreferences(compress)
                    customDialogPopup.dismiss()
                },
                resources.getString(R.string.button_no), {
                    compress = false
                    saveCompressStateToPreferences(compress)
                    customDialogPopup.dismiss()
                }
            )
            customDialogPopup.show()
        }

        binding?.navHeader?.logout?.setOnClickListener {
            (activity as MainActivity).logout()
        }

        val versionName = BuildConfig.VERSION_NAME
        binding?.navHeader?.versionName?.text = versionName

        val versionCode = "(" + BuildConfig.VERSION_CODE.toString() + ")"
        binding?.navHeader?.versionCode?.text = versionCode

        if (listLinkSide != null) {
            addSideViews(listLinkSide)
        }
    }

    private fun addLinkViews(listLink: ArrayList<Link>?) {

        linkAreaLayoutBinding?.linkUtiliContainer?.removeAllViews()

        if (listLink.isNullOrEmpty())
            return

        for (item in listLink) {

            val itemBinding = LinkDynamicItemBinding.inflate(LayoutInflater.from(context), linkAreaLayoutBinding?.linkUtiliContainer, false)
            val rowView: View = itemBinding.root

            val labelName: TextView = itemBinding.linkName
            val labelIcon: ImageView = itemBinding.linkIcon

            setBitmapFromUrl(item.image, labelIcon, requireActivity())

            val linkItem: RelativeLayout = itemBinding.linkItem

            labelName.text = item.name

            linkAreaLayoutBinding?.linkUtiliContainer?.addView(rowView)

            linkItem.setOnClickListener {
                when (item.type) {
                    webviewType -> {
                        bundleDeepLink.putString("link_deeplink", item.name)
                        bundleDeepLink.putString("url_deeplink", item.link)
                        currentActivity.openDetail(Globals.WEB_VIEW, bundleDeepLink)
                    }
                    appLinkType -> {
                        if (item.link == "waw3://contacts") {
                            currentActivity.openDetail(Globals.RUBRICA_REALTIME, null)
                        }
                    }
                    appType -> {
                        // val params = Bundle()
                        // params.putString(Mapping.WAW3Key.WAW3_LINK, item.analyticsLabel)
                        // trackEvent(params)
                        currentActivity.openApp(item.appLinkAndroid)
                    }
                    extType -> {
                        currentActivity.openBrowser(item.link)
                    }
                }
            }
        }
        linkAreaLayoutBinding?.linkUtiliContainer?.visibility = View.VISIBLE
    }

    private fun addSideViews(listLinkSide: ArrayList<LinkSide>?) {

        binding?.navHeader?.navHeaderContainer?.removeAllViews()

        if (listLinkSide.isNullOrEmpty())
            return

        for (item in listLinkSide) {

            val itemBinding = NavHeaderItemBinding.inflate(LayoutInflater.from(context), binding?.navHeader?.navHeaderContainer, false)
            val rowView = itemBinding.root

            val labelName: TextView = itemBinding.labelName
            labelName.text = item.name

            if (isDarkMode)
                labelName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            else
                labelName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))

            binding?.navHeader?.navHeaderContainer?.addView(rowView)

            labelName.setOnClickListener {
                closeSideMenu()
                when (item.type) {
                    webviewType -> {
                        bundleDeepLink.putString("link_deeplink", item.name)
                        bundleDeepLink.putString("url_deeplink", item.link)
                        currentActivity.openDetail(Globals.WEB_VIEW, bundleDeepLink)
                    }
                    appLinkType -> {
                        currentActivity.openDetail(Globals.RUBRICA_REALTIME, null)
                    }
                    appType -> {
                        currentActivity.openApp(item.appLinkAndroid)
                    }

                    extType -> {
                        currentActivity.openBrowser(item.link)
                    }
                }
            }
        }
        binding?.navHeader?.navHeaderContainer?.visibility = View.VISIBLE
    }

    private fun attachTabListener() {
        binding?.mainContent?.tabHome?.setOnClickListener(tabHomeClick)
        binding?.mainContent?.tabWorkingArea?.setOnClickListener(tabWorkingAreaClick)
        binding?.mainContent?.tabLinkArea?.setOnClickListener(tabLinkAreaClick)
        binding?.mainContent?.tabMenu?.setOnClickListener(tabMenuClick)
    }

    private fun resetBackgroundTabNav() {
        val start = binding?.mainContent?.tabHome?.x!! + (binding?.mainContent?.tabHome?.width!! / 2)
        val end = binding?.mainContent?.tabMenu?.x!! + (binding?.mainContent?.tabMenu?.width!! / 2)
        val delta = (end - start) / 4

        binding?.mainContent?.backgroundBottomBar?.x = -(delta * 2)
        binding?.mainContent?.tabHome?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ico_bottom_home_rvd
            )
        )
        defaultX = (binding?.mainContent?.tabHome?.width!!).toFloat()
    }

    private fun getTabInstanceByPosition(position: Int): ImageView {
        return when (position) {
            1 -> binding?.mainContent?.tabHome!!
            2 -> binding?.mainContent?.tabWorkingArea!!
            3 -> binding?.mainContent?.tabLinkArea!!
            else -> binding?.mainContent?.tabHome!!
        }
    }

    private fun getDrawableTabInstanceByPosition(position: Int, rvdType: Boolean): Drawable {
        when (position) {
            1 -> {
                return if (!rvdType)
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_home)!!
                else
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_home_rvd)!!
            }
            2 -> {
                return if (!rvdType)
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_working_area)!!
                else
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_working_area_rvd)!!
            }
            3 -> {
                return if (!rvdType)
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ico_bottom_link_area
                    )!!
                else
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ico_bottom_link_area_rvd
                    )!!
            }
            else -> {
                return if (!rvdType)
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_home)!!
                else
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_home_rvd)!!
            }
        }
    }

    private var tabHomeClick: View.OnClickListener = View.OnClickListener {
        translateAnimation(
            TAB_INDEX_HOME,
            binding?.mainContent?.tabHome!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_home)!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_home_rvd)!!
        )
        binding?.mainContent?.viewPager?.currentItem = TAB_INDEX_HOME - 1
    }

    private var tabWorkingAreaClick: View.OnClickListener = View.OnClickListener {
        translateAnimation(
            TAB_INDEX_WORKING_AREA,
            binding?.mainContent?.tabWorkingArea!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_working_area)!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_working_area_rvd)!!
        )
        binding?.mainContent?.viewPager?.currentItem = TAB_INDEX_WORKING_AREA - 1
    }

    private var tabLinkAreaClick: View.OnClickListener = View.OnClickListener {
        translateAnimation(
            TAB_INDEX_LINK_AREA,
            binding?.mainContent?.tabLinkArea!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_link_area)!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_link_area_rvd)!!
        )
        binding?.mainContent?.viewPager?.currentItem = TAB_INDEX_LINK_AREA - 1
    }

    private var tabMenuClick: View.OnClickListener = View.OnClickListener {
        openSideMenu()
    }

    private fun closeSideMenu() {
        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.END)!!) {
            binding?.drawerLayout?.closeDrawer(GravityCompat.END)
            binding?.mainContent?.tabMenu?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ico_bottom_menu
                )
            )
        }
    }

    private fun openSideMenu() {
        binding?.drawerLayout?.isEnabled = true
        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.END)!!) {
            binding?.drawerLayout?.closeDrawer(GravityCompat.END)
            binding?.mainContent?.tabMenu?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ico_bottom_menu
                )
            )
        } else {
            binding?.drawerLayout?.openDrawer(GravityCompat.END)
            binding?.mainContent?.tabMenu?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ico_bottom_close
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
            val offset: Float = binding?.mainContent?.backgroundBottomBar?.x!! + ((tab - previousTab) * defaultX)
            previousTab = tab
            val valueAnimator: ValueAnimator = ObjectAnimator.ofFloat(
                binding?.mainContent?.backgroundBottomBar,
                "translationX",
                offset
            ).apply {
                duration = transitionTime
            }

            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animationFinish = true
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
        drawableFrom: Drawable,
        drawableTo: Drawable
    ) {
        val transitionDrawable = TransitionDrawable(arrayOf(drawableFrom, drawableTo))

        imageView.setImageDrawable(transitionDrawable)
        transitionDrawable.startTransition(transitionTime.toInt())
        transitionDrawable.isCrossFadeEnabled = false
    }

    private fun resetAllImages() {
        binding?.mainContent?.tabHome?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ico_bottom_home))
        binding?.mainContent?.tabWorkingArea?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ico_bottom_working_area
            )
        )
        binding?.mainContent?.tabLinkArea?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ico_bottom_link_area
            )
        )
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

        val mModel: DarkModeModel?
        isDarkMode = !isDarkMode
        saveDarkModeStateToPreferences(isDarkMode)

        if (isDarkMode)
            currentActivity.setTheme(R.style.AppTheme)
        else
            currentActivity.setTheme(R.style.AppThemeLight)

        mModel = DarkModeModel(requireContext())
        binding?.temp = mModel
        binding?.presenter = darkModePresenter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}