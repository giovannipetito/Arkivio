package it.giovanni.arkivio.viewinterfaces

import it.giovanni.arkivio.model.DarkModeModel

interface IDarkMode {

    interface Presenter {
        fun onShowDataModel(model: DarkModeModel?)
        fun onSetLayout(model: DarkModeModel?)
    }

    interface View {
        fun onShowDataModel(model: DarkModeModel?)
        fun onSetLayout(model: DarkModeModel?)
    }
}