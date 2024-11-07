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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import it.giovanni.arkivio.BuildConfig
import it.giovanni.arkivio.R
import it.giovanni.arkivio.activities.MainActivity
import it.giovanni.arkivio.adapters.HomeFragmentAdapter
import it.giovanni.arkivio.model.LinkSide
import it.giovanni.arkivio.customview.dialog.CoreDialog
import it.giovanni.arkivio.databinding.MainLayoutBinding
import it.giovanni.arkivio.databinding.NavHeaderItemBinding
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.SharedPreferencesManager.loadCompressStateFromPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.saveCompressStateToPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.saveDarkModeStateToPreferences
import it.giovanni.arkivio.utils.UserFactory
import it.giovanni.arkivio.utils.Utils.getRoundBitmap
import it.giovanni.arkivio.viewinterfaces.IDarkMode

class MainFragment : BaseFragment(SectionType.MAIN), IDarkMode.View {

    companion object {
        var TAB_INDEX_HOMEPAGE: Int = 1
        var TAB_INDEX_LEARNING: Int = 2
        var TAB_INDEX_TRAINING: Int = 3
    }

    private var layoutBinding: MainLayoutBinding? = null
    private val binding get() = layoutBinding
    private var darkModePresenter: DarkModePresenter? = null
    private var model: DarkModeModel? = null

    private var defaultX: Float = 0f
    private var transitionTime: Long = 100
    var animationFinish = true
    private var previousTab: Int = TAB_INDEX_HOMEPAGE
    private lateinit var fragmentAdapter: HomeFragmentAdapter
    private val endScale = 0.9f
    private var listLinkSide: ArrayList<LinkSide>? = null
    private val webviewType = "webview"
    private val appLinkType = "inAppLink"
    private val appType = "app"
    private val extType = "ext"
    private var bundleDeepLink: Bundle = Bundle()

    private lateinit var coreDialog: CoreDialog
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

            val dialog = CoreDialog(currentActivity, R.style.CoreDialogTheme)
            dialog.setCancelable(false)
            dialog.setTitle("", "")
            dialog.setMessage(resources.getString(R.string.switch_mode_message))

            dialog.setButtons(
                resources.getString(R.string.button_confirm), {
                    dialog.dismiss()
                    onSetLayout(model)
                    (activity as MainActivity).logout()
                },
                resources.getString(R.string.button_cancel), {
                    dialog.dismiss()
                    binding?.navHeader?.switchMode?.isChecked = !isDarkMode
                }
            )
            dialog.show()
        }
    }

    private fun draw() {

        val displayMetrics = DisplayMetrics()
        currentActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        listLinkSide =
            if (UserFactory.getInstance().listLinkSide != null)
                UserFactory.getInstance().listLinkSide
            else initSide()

        attachViewPager()
        attachSideMenu(listLinkSide)
        attachTabListener()
        resetBackgroundTabNav()

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
        binding?.mainContent?.viewPager?.currentItem = (TAB_INDEX_HOMEPAGE - 1)

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
                        R.drawable.ico_menu
                    )
                )
            }
        })

        binding?.drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        binding?.navHeader?.settings?.setOnClickListener {
            coreDialog = CoreDialog(currentActivity, R.style.CoreDialogTheme)
            coreDialog.setCancelable(false)
            coreDialog.setTitle(context?.resources?.getString(R.string.settings)!!, "")
            coreDialog.setMessage(context?.resources?.getString(R.string.settings_message)!!)

            coreDialog.setButtons(
                resources.getString(R.string.button_yes), {
                    compress = true
                    saveCompressStateToPreferences(compress)
                    coreDialog.dismiss()
                },
                resources.getString(R.string.button_no), {
                    compress = false
                    saveCompressStateToPreferences(compress)
                    coreDialog.dismiss()
                }
            )
            coreDialog.show()
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
        binding?.mainContent?.tabHomepage?.setOnClickListener(tabHomepageClick)
        binding?.mainContent?.tabLearning?.setOnClickListener(tabLearningClick)
        binding?.mainContent?.tabTraining?.setOnClickListener(tabTrainingClick)
        binding?.mainContent?.tabMenu?.setOnClickListener(tabMenuClick)
    }

    private fun resetBackgroundTabNav() {
        val start = binding?.mainContent?.tabHomepage?.x!! + (binding?.mainContent?.tabHomepage?.width!! / 2)
        val end = binding?.mainContent?.tabMenu?.x!! + (binding?.mainContent?.tabMenu?.width!! / 2)
        val delta = (end - start) / 4

        binding?.mainContent?.backgroundBottomBar?.x = -(delta * 2)
        binding?.mainContent?.tabHomepage?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ico_home_rvd
            )
        )
        defaultX = (binding?.mainContent?.tabHomepage?.width!!).toFloat()
    }

    private fun getTabInstanceByPosition(position: Int): ImageView {
        return when (position) {
            1 -> binding?.mainContent?.tabHomepage!!
            2 -> binding?.mainContent?.tabLearning!!
            3 -> binding?.mainContent?.tabTraining!!
            else -> binding?.mainContent?.tabHomepage!!
        }
    }

    private fun getDrawableTabInstanceByPosition(position: Int, rvdType: Boolean): Drawable {
        when (position) {
            1 -> {
                return if (!rvdType)
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_home)!!
                else
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_home_rvd)!!
            }
            2 -> {
                return if (!rvdType)
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_learning)!!
                else
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_learning_rvd)!!
            }
            3 -> {
                return if (!rvdType)
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_training)!!
                else
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_training_rvd)!!
            }
            else -> {
                return if (!rvdType)
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_home)!!
                else
                    ContextCompat.getDrawable(requireContext(), R.drawable.ico_home_rvd)!!
            }
        }
    }

    private var tabHomepageClick: View.OnClickListener = View.OnClickListener {
        translateAnimation(
            TAB_INDEX_HOMEPAGE,
            binding?.mainContent?.tabHomepage!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ico_home)!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ico_home_rvd)!!
        )
        binding?.mainContent?.viewPager?.currentItem = TAB_INDEX_HOMEPAGE - 1
    }

    private var tabLearningClick: View.OnClickListener = View.OnClickListener {
        translateAnimation(
            TAB_INDEX_LEARNING,
            binding?.mainContent?.tabLearning!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ico_learning)!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ico_learning_rvd)!!
        )
        binding?.mainContent?.viewPager?.currentItem = TAB_INDEX_LEARNING - 1
    }

    private var tabTrainingClick: View.OnClickListener = View.OnClickListener {
        translateAnimation(
            TAB_INDEX_TRAINING,
            binding?.mainContent?.tabTraining!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ico_training)!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ico_training_rvd)!!
        )
        binding?.mainContent?.viewPager?.currentItem = TAB_INDEX_TRAINING - 1
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
                    R.drawable.ico_menu
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
                    R.drawable.ico_menu
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
        binding?.mainContent?.tabHomepage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ico_home))
        binding?.mainContent?.tabLearning?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ico_learning
            )
        )
        binding?.mainContent?.tabTraining?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ico_training
            )
        )
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