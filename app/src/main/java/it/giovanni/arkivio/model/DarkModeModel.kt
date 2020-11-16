@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
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
            viewColor = context.resources.getColor(R.color.grey_3)
            labelColor = context.resources.getColor(R.color.white)
            bottomBarColor = context.resources.getColor(R.color.white)
            label = context.resources.getString(R.string.label_dark_mode)
            icoDark = context.resources.getDrawable(R.drawable.ico_dark_mode_light)
            icoLight = context.resources.getDrawable(R.drawable.ico_light_mode_light)
            sideNavBar = context.resources.getDrawable(R.drawable.side_nav_bar_dark)
            roundCorner = context.resources.getDrawable(R.drawable.round_corner_4)
            backgroundTabColor = context.resources.getDrawable(R.drawable.background_dark_mode)
            backgroundColor = context.resources.getColor(R.color.black)
            viewVisibility = View.GONE
        } else {
            viewColor = context.resources.getColor(R.color.dark)
            labelColor = context.resources.getColor(R.color.dark)
            bottomBarColor = context.resources.getColor(R.color.black)
            label = context.resources.getString(R.string.label_light_mode)
            icoDark = context.resources.getDrawable(R.drawable.ico_dark_mode_dark)
            icoLight = context.resources.getDrawable(R.drawable.ico_light_mode_dark)
            sideNavBar = context.resources.getDrawable(R.drawable.side_nav_bar_light)
            roundCorner = context.resources.getDrawable(R.drawable.round_corner_1)
            backgroundTabColor = context.resources.getDrawable(R.drawable.background_light_mode)
            backgroundColor = context.resources.getColor(R.color.white)
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