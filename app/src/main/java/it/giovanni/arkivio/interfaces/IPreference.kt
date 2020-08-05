package it.giovanni.arkivio.interfaces

interface IPreference {

    // Interfaccia implementata dal Model per esser chiamato dal Presenter
    interface UpdateModel {
        fun cancelRequest()
        fun getUserPreference(keyPreference: String): String
        fun setUserPreference(keyPreference: String, value: String)
    }

    // Interfaccia implementata dalla View
    interface UpdatesView {
        fun hideProgressDialog()
        fun showProgressDialog()
        fun isSafe(): Boolean
    }

    // Interfaccia implementata dal Presenter per ricevere gli eventi utente dalla View
    interface UserEvents {
        fun getUserPreference(keyPreference: String): String
        fun setUserPreference(keyPreference: String, value: String)
        fun cancelRequest()
    }

    // Interfaccia implementata dal Presenter per esser contattata dal Model
    interface StateChangeEvents {
        // nothing
    }
}