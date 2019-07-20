package it.giovanni.kotlin.model

import it.giovanni.kotlin.App
import it.giovanni.kotlin.interfaces.IPreference

class PreferenceModel: BaseModel(), IPreference.UpdateModel {

    override fun cancelRequest() {
    }

    override fun getUserPreference(keyPreference: String): String {
        return App.getRepository()!!.getPreference(keyPreference)
    }

    override fun setUserPreference(keyPreference: String, value: String) {
        App.getRepository()!!.setPreference(keyPreference, value)
    }
}