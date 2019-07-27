package it.giovanni.kotlin.fragments.detail.preference

import android.content.Intent
import android.os.Bundle
import android.view.View
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.DetailFragment
import it.giovanni.kotlin.interfaces.IPreference
import it.giovanni.kotlin.model.PreferenceModel
import it.giovanni.kotlin.persistence.UserPreferencesRepository
import it.giovanni.kotlin.presenter.PreferencePresenter
import it.giovanni.kotlin.utils.Globals
import it.giovanni.kotlin.utils.UserFactory
import kotlinx.android.synthetic.main.preference_layout.*

class PreferenceFragment: DetailFragment(), IPreference.UpdatesView {

    var presenter: IPreference.UserEvents? = null

    override fun getLayout(): Int {
        return R.layout.preference_layout
    }

    override fun getTitle(): Int {
        return R.string.preference_title
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

        presenter = PreferencePresenter(this, PreferenceModel())
        contacts_text.text = presenter!!.getUserPreference(UserPreferencesRepository.KEY_PREFERENCE_CONTACT)
        contacts_container.setOnClickListener {
            currentActivity.openDetail(Globals.PREFERENCE_LIST, null, this@PreferenceFragment, Globals.REQUEST_CODE_USER_PREFERENCE)
        }

        user_contacts_text.text = UserFactory.getInstance().contacts
    }

    @Suppress("UNCHECKED_CAST")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Globals.REQUEST_CODE_USER_PREFERENCE && data != null && data.hasExtra(Globals.BACK_PARAM_KEY_USER_PREFERENCE)) {
            contacts_text.text = data.getStringExtra(Globals.BACK_PARAM_KEY_USER_PREFERENCE) as String
            presenter!!.setUserPreference(UserPreferencesRepository.KEY_PREFERENCE_CONTACT, contacts_text.text.toString())

            UserFactory.getInstance().contacts = contacts_text.text.toString()
            user_contacts_text.text = UserFactory.getInstance().contacts
        }
    }
}