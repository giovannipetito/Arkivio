@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import it.giovanni.arkivio.BR
import it.giovanni.arkivio.R

class DarkModeModel : BaseObservable {

    private var colore = 0
    private var sfondo = false
    private var sideNavBar: Drawable? = null
    private var icoDarkMode: Drawable? = null
    private var icoLightMode: Drawable? = null
    private var visibilita = 0

//    @get:Bindable
//    var spinner: Drawable? = null
//        set(spinner) {
//            field = spinner
//            notifyPropertyChanged(BR.spinner)
//        }

    constructor(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val isDarkMode = preferences.getBoolean("DARK_MODE", false)
        if (isDarkMode) {
            colore = context.resources.getColor(R.color.white)
            sideNavBar = context.resources.getDrawable(R.drawable.side_nav_bar_dark)
            icoDarkMode = context.resources.getDrawable(R.drawable.ico_dark_mode_light)
            icoLightMode = context.resources.getDrawable(R.drawable.ico_light_mode_light)
            visibilita = View.GONE
        } else {
            colore = context.resources.getColor(R.color.colorPrimary)
            sideNavBar = context.resources.getDrawable(R.drawable.side_nav_bar_light)
            icoDarkMode = context.resources.getDrawable(R.drawable.ico_dark_mode_dark)
            icoLightMode = context.resources.getDrawable(R.drawable.ico_light_mode_dark)
            visibilita = View.VISIBLE
        }
    }

    @Bindable
    fun getColore(): Int {
        return colore
    }

    fun setColore(colore: Int) {
        this.colore = colore
        notifyPropertyChanged(BR.colore)
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
    fun getIcoDarkMode(): Drawable? {
        return icoDarkMode
    }

    fun setIcoDarkMode(icoDarkMode: Drawable?) {
        this.icoDarkMode = icoDarkMode
         notifyPropertyChanged(BR.icoDarkMode)
    }

    @Bindable
    fun getIcoLightMode(): Drawable? {
        return icoLightMode
    }

    fun setIcoLightMode(icoLightMode: Drawable?) {
        this.icoLightMode = icoLightMode
         notifyPropertyChanged(BR.icoLightMode)
    }

//    @Bindable
//    fun isSfondo(): Boolean {
//        return sfondo
//    }
//
//    fun setSfondo(sfondo: Boolean) {
//        this.sfondo = sfondo
//        notifyPropertyChanged(BR.sfondo)
//    }
//
//    @Bindable
//    fun getVisibilita(): Int {
//        return visibilita
//    }
//
//    fun setVisibilita(visibilita: Int) {
//        this.visibilita = visibilita
//        notifyPropertyChanged(BR.visibilita)
//    }
}