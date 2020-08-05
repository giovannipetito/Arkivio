package it.giovanni.arkivio.presenter

import it.giovanni.arkivio.interfaces.IPreference

class PreferencePresenter(
    view: IPreference.UpdatesView,
    model: IPreference.UpdateModel) : BasePresenter<IPreference.UpdatesView, IPreference.UpdateModel>(view, model),
    IPreference.UserEvents,
    IPreference.StateChangeEvents {

    override fun getUserPreference(keyPreference: String): String {
        return model.getUserPreference(keyPreference)
    }

    override fun setUserPreference(keyPreference: String, value: String) {
        model.setUserPreference(keyPreference, value)
    }

    override fun cancelRequest() {
    }
}