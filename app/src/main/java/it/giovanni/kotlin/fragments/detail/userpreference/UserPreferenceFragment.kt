package it.giovanni.kotlin.fragments.detail.userpreference

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.DetailFragment
import it.giovanni.kotlin.interfaces.IUserPreference
import it.giovanni.kotlin.model.UserPreferenceModel
import it.giovanni.kotlin.persistence.UserPreferencesRepository
import it.giovanni.kotlin.presenter.UserPreferencePresenter
import it.giovanni.kotlin.utils.Globals
import kotlinx.android.synthetic.main.user_preference_layout.*

class UserPreferenceFragment: DetailFragment(), IUserPreference.UpdatesView {

    var presenter: IUserPreference.UserEvents? = null

    override fun getLayout(): Int {
        return R.layout.user_preference_layout
    }

    override fun getTitle(): Int {
        return R.string.user_preference_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return false
    }

    override fun closeAction(): Boolean {
        return true
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(search_string: String) {
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = UserPreferencePresenter(this, UserPreferenceModel())
        contacts_text.text = presenter!!.getUserPreference(UserPreferencesRepository.KEY_PREFERENCE_CONTACT)
        Log.i("USERTAG", "usertag 1: " + presenter!!.getUserPreference(UserPreferencesRepository.KEY_PREFERENCE_CONTACT))
        contacts_container.setOnClickListener {
            currentActivity.openDetail(Globals.USER_PREFERENCE_LIST, null, this@UserPreferenceFragment, Globals.REQUEST_CODE_USER_PREFERENCE)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Globals.REQUEST_CODE_USER_PREFERENCE && data != null && data.hasExtra(Globals.BACK_PARAM_KEY_USER_PREFERENCE)) {
            contacts_text.text = data.getStringExtra(Globals.BACK_PARAM_KEY_USER_PREFERENCE) as String
            presenter!!.setUserPreference(UserPreferencesRepository.KEY_PREFERENCE_CONTACT, contacts_text.text.toString())
            Log.i("USERTAG", "usertag 2: " + presenter!!.getUserPreference(UserPreferencesRepository.KEY_PREFERENCE_CONTACT))
        }
    }
}