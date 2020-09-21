package it.giovanni.arkivio.presenter

import android.content.Context
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.viewinterfaces.IDarkMode

class DarkModePresenter(private val view: IDarkMode.View, private val context: Context) : IDarkMode.Presenter {

    override fun onShowDataModel(model: DarkModeModel?) {
        view.onShowDataModel(model)
    }

    override fun onSetLayout(model: DarkModeModel?) {
        view.onSetLayout(model)
    }
}