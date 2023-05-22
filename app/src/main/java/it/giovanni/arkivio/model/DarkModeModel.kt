package it.giovanni.arkivio.model

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BaseObservable
import it.giovanni.arkivio.BR
import it.giovanni.arkivio.R
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadDarkModeStateFromPreferences

class DarkModeModel(context: Context) : BaseObservable() {

    private var labelColor: Int? = 0
    private var bottomBarColor: Int? = 0
    private var label: String? = null
    private var icoDark: Drawable? = null
    private var icoLight: Drawable? = null
    private var sideNavBar: Drawable? = null
    private var backgroundTabColor: Drawable? = null
    private var backgroundColor: Int? = 0

    init {
        val isDarkMode = loadDarkModeStateFromPreferences()
        if (isDarkMode) {
            labelColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
            bottomBarColor = ResourcesCompat.getColor(context.resources, R.color.verde, null)
            label = context.resources.getString(R.string.label_dark_mode)
            icoDark = ResourcesCompat.getDrawable(context.resources, R.drawable.ico_dark_mode_light, null)
            icoLight = ResourcesCompat.getDrawable(context.resources, R.drawable.ico_light_mode_light, null)
            sideNavBar = ResourcesCompat.getDrawable(context.resources, R.drawable.side_nav_bar_dark, null)
            backgroundTabColor = ResourcesCompat.getDrawable(context.resources, R.drawable.background_dark_mode, null)
            backgroundColor = ResourcesCompat.getColor(context.resources, R.color.black_1, null)
        } else {
            labelColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimaryDark, null)
            bottomBarColor = ResourcesCompat.getColor(context.resources, R.color.rosso, null)
            label = context.resources.getString(R.string.label_light_mode)
            icoDark = ResourcesCompat.getDrawable(context.resources, R.drawable.ico_dark_mode_dark, null)
            icoLight = ResourcesCompat.getDrawable(context.resources, R.drawable.ico_light_mode_dark, null)
            sideNavBar = ResourcesCompat.getDrawable(context.resources, R.drawable.side_nav_bar_light, null)
            backgroundTabColor = ResourcesCompat.getDrawable(context.resources, R.drawable.background_light_mode, null)
            backgroundColor = ResourcesCompat.getColor(context.resources, R.color.white, null)
        }
    }

    fun getLabelColor(): Int? {
        return labelColor
    }

    fun setLabelColor(labelColor: Int?) {
        this.labelColor = labelColor
        // notifyPropertyChanged(BR.labelColor)
    }

    fun getBottomBarColor(): Int? {
        return bottomBarColor
    }

    fun setBottomBarColor(bottomBarColor: Int?) {
        this.bottomBarColor = bottomBarColor
    }

    fun getLabel(): String? {
        return label
    }

    fun setLabel(label: String?) {
        this.label = label
    }

    fun getIcoDark(): Drawable? {
        return icoDark
    }

    fun setIcoDark(icoDark: Drawable?) {
        this.icoDark = icoDark
    }

    fun getIcoLight(): Drawable? {
        return icoLight
    }

    fun setIcoLight(icoLight: Drawable?) {
        this.icoLight = icoLight
    }

    fun getSideNavBar(): Drawable? {
        return sideNavBar
    }

    fun setSideNavBar(sideNavBar: Drawable?) {
        this.sideNavBar = sideNavBar
    }

    fun getBackgroundTabColor(): Drawable? {
        return backgroundTabColor
    }

    fun setBackgroundTabColor(backgroundTabColor: Drawable?) {
        this.backgroundTabColor = backgroundTabColor
    }

    fun getBackgroundColor(): Int? {
        return backgroundColor
    }

    fun setBackgroundColor(backgroundColor: Int?) {
        this.backgroundColor = backgroundColor
    }
}