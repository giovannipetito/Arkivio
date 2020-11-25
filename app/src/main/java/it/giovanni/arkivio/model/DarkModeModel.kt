package it.giovanni.arkivio.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import it.giovanni.arkivio.BR
import it.giovanni.arkivio.R
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadDarkModeStateFromPreferences

class DarkModeModel : BaseObservable {

    private var viewColor: Int? = 0
    private var labelColor: Int? = 0
    private var bottomBarColor: Int? = 0
    private var label: String? = null
    private var icoDark: Drawable? = null
    private var icoLight: Drawable? = null
    private var sideNavBar: Drawable? = null
    private var roundCorner: Drawable? = null
    private var backgroundTabColor: Drawable? = null
    private var backgroundColor: Int? = 0
    private var viewVisibility = 0

//    @get:Bindable
//    var spinner: Drawable? = null
//        set(spinner) {
//            field = spinner
//            notifyPropertyChanged(BR.spinner)
//        }

    constructor(context: Context) {
        val isDarkMode = loadDarkModeStateFromPreferences()
        if (isDarkMode) {
            viewColor = ResourcesCompat.getColor(context.resources, R.color.grey_3, null)
            labelColor = ResourcesCompat.getColor(context.resources, R.color.white, null)
            bottomBarColor = ResourcesCompat.getColor(context.resources, R.color.white, null)
            label = context.resources.getString(R.string.label_dark_mode)
            icoDark = ResourcesCompat.getDrawable(context.resources, R.drawable.ico_dark_mode_light, null)
            icoLight = ResourcesCompat.getDrawable(context.resources, R.drawable.ico_light_mode_light, null)
            sideNavBar = ResourcesCompat.getDrawable(context.resources, R.drawable.side_nav_bar_dark, null)
            roundCorner = ResourcesCompat.getDrawable(context.resources, R.drawable.round_corner_4, null)
            backgroundTabColor = ResourcesCompat.getDrawable(context.resources, R.drawable.background_dark_mode, null)
            backgroundColor = ResourcesCompat.getColor(context.resources, R.color.black, null)
            viewVisibility = View.GONE
        } else {
            viewColor = ResourcesCompat.getColor(context.resources, R.color.dark, null)
            labelColor = ResourcesCompat.getColor(context.resources, R.color.dark, null)
            bottomBarColor = ResourcesCompat.getColor(context.resources, R.color.black, null)
            label = context.resources.getString(R.string.label_light_mode)
            icoDark = ResourcesCompat.getDrawable(context.resources, R.drawable.ico_dark_mode_dark, null)
            icoLight = ResourcesCompat.getDrawable(context.resources, R.drawable.ico_light_mode_dark, null)
            sideNavBar = ResourcesCompat.getDrawable(context.resources, R.drawable.side_nav_bar_light, null)
            roundCorner = ResourcesCompat.getDrawable(context.resources, R.drawable.round_corner_1, null)
            backgroundTabColor = ResourcesCompat.getDrawable(context.resources, R.drawable.background_light_mode, null)
            backgroundColor = ResourcesCompat.getColor(context.resources, R.color.white, null)
            viewVisibility = View.VISIBLE
        }
    }

    @Bindable
    fun getViewColor(): Int? {
        return viewColor
    }

    fun setViewColor(viewColor: Int?) {
        this.viewColor = viewColor
        notifyPropertyChanged(BR.viewColor)
    }

    @Bindable
    fun getLabelColor(): Int? {
        return labelColor
    }

    fun setLabelColor(labelColor: Int?) {
        this.labelColor = labelColor
        notifyPropertyChanged(BR.labelColor)
    }

    @Bindable
    fun getBottomBarColor(): Int? {
        return bottomBarColor
    }

    fun setBottomBarColor(bottomBarColor: Int?) {
        this.bottomBarColor = bottomBarColor
        notifyPropertyChanged(BR.bottomBarColor)
    }

    @Bindable
    fun getLabel(): String? {
        return label
    }

    fun setLabel(label: String?) {
        this.label = label
        notifyPropertyChanged(BR.label)
    }

    @Bindable
    fun getIcoDark(): Drawable? {
        return icoDark
    }

    fun setIcoDark(icoDark: Drawable?) {
        this.icoDark = icoDark
         notifyPropertyChanged(BR.icoDark)
    }

    @Bindable
    fun getIcoLight(): Drawable? {
        return icoLight
    }

    fun setIcoLight(icoLight: Drawable?) {
        this.icoLight = icoLight
         notifyPropertyChanged(BR.icoLight)
    }

    @Bindable
    fun getSideNavBar(): Drawable? {
        return sideNavBar
    }

    fun setSideNavBar(sideNavBar: Drawable?) {
        this.sideNavBar = sideNavBar
        notifyPropertyChanged(BR.sideNavBar)
    }

    @Bindable
    fun getRoundCorner(): Drawable? {
        return roundCorner
    }

    fun setRoundCorner(roundCorner: Drawable?) {
        this.roundCorner = roundCorner
        notifyPropertyChanged(BR.roundCorner)
    }

    @Bindable
    fun getBackgroundTabColor(): Drawable? {
        return backgroundTabColor
    }

    fun setBackgroundTabColor(backgroundTabColor: Drawable?) {
        this.backgroundTabColor = backgroundTabColor
        notifyPropertyChanged(BR.backgroundTabColor)
    }

    @Bindable
    fun getBackgroundColor(): Int? {
        return backgroundColor
    }

    fun setBackgroundColor(backgroundColor: Int?) {
        this.backgroundColor = backgroundColor
        notifyPropertyChanged(BR.backgroundColor)
    }

    @Bindable
    fun getViewVisibility(): Int {
        return viewVisibility
    }

    fun setViewVisibility(viewVisibility: Int) {
        this.viewVisibility = viewVisibility
        notifyPropertyChanged(BR.viewVisibility)
    }
}