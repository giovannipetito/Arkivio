package it.giovanni.kotlin.presenter

import it.giovanni.kotlin.interfaces.IUserPreference

class UserPreferencePresenter(
    view: IUserPreference.UpdatesView,
    model: IUserPreference.UpdateModel) : BasePresenter<IUserPreference.UpdatesView, IUserPreference.UpdateModel>(view, model),
    IUserPreference.UserEvents,
    IUserPreference.StateChangeEvents {

    override fun getUserPreference(keyPreference: String): String {
        return model.getUserPreference(keyPreference)
    }

    override fun setUserPreference(keyPreference: String, value: String) {
        model.setUserPreference(keyPreference, value)
    }

    override fun cancelRequest() {
    }
}