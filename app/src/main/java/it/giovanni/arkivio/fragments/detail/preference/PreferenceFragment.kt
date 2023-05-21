package it.giovanni.arkivio.fragments.detail.preference

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.PreferenceLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.interfaces.IPreference
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.model.PreferenceModel
import it.giovanni.arkivio.persistence.UserPreferencesRepository
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.presenter.PreferencePresenter
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.UserFactory

class PreferenceFragment: DetailFragment(), IPreference.UpdatesView {

    private var layoutBinding: PreferenceLayoutBinding? = null
    private val binding get() = layoutBinding

    var presenter: IPreference.UserEvents? = null

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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = PreferenceLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = PreferencePresenter(this, PreferenceModel())
        binding?.contactsText?.text = presenter?.getUserPreference(UserPreferencesRepository.KEY_PREFERENCE_CONTACT)
        binding?.contactsContainer?.setOnClickListener {
            currentActivity.openDetail(Globals.PREFERENCE_LIST, null, this@PreferenceFragment, Globals.REQUEST_CODE_USER_PREFERENCE)
        }

        binding?.userContactsText?.text = UserFactory.getInstance().contacts
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Globals.REQUEST_CODE_USER_PREFERENCE && data != null && data.hasExtra(Globals.BACK_PARAM_KEY_USER_PREFERENCE)) {
            binding?.contactsText?.text = data.getStringExtra(Globals.BACK_PARAM_KEY_USER_PREFERENCE) as String
            presenter?.setUserPreference(UserPreferencesRepository.KEY_PREFERENCE_CONTACT, binding?.contactsText?.text.toString())

            UserFactory.getInstance().contacts = binding?.contactsText?.text.toString()
            binding?.userContactsText?.text = UserFactory.getInstance().contacts
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}