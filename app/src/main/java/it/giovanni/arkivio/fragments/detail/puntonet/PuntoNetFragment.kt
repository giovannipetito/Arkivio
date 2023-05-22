package it.giovanni.arkivio.fragments.detail.puntonet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.archive.Toaster
import it.giovanni.arkivio.R
import it.giovanni.arkivio.activities.NavigationActivity
import it.giovanni.arkivio.databinding.PuntonetLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals

class PuntoNetFragment : DetailFragment() {

    private var layoutBinding: PuntonetLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.puntonet_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
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
        layoutBinding = PuntonetLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.labelArklib?.text = Toaster.getMessage()

        binding?.labelNavigationComponent?.setOnClickListener {
            startActivity(Intent(context, NavigationActivity::class.java))
        }
        binding?.labelUserInput?.setOnClickListener {
            currentActivity.openDetail(Globals.MVVM_USER_INPUT, null)
        }
        binding?.labelLoginInput?.setOnClickListener {
            currentActivity.openDetail(Globals.MVVM_LOGIN, null)
        }
        binding?.labelMvvmUsers?.setOnClickListener {
            currentActivity.openDetail(Globals.MVVM_USERS, null)
        }
        binding?.labelCoroutineHome?.setOnClickListener {
            currentActivity.openDetail(Globals.COROUTINE_HOME, null)
        }
        binding?.labelPaging?.setOnClickListener {
            currentActivity.openDetail(Globals.PAGING, null)
        }
        binding?.labelUsersHome?.setOnClickListener {
            currentActivity.openDetail(Globals.USERS_HOME, null)
        }
        binding?.labelRxHome?.setOnClickListener {
            currentActivity.openDetail(Globals.RX_HOME, null)
        }
        binding?.labelDependencyInjection?.setOnClickListener {
            currentActivity.openDetail(Globals.DEPENDENCY_INJECTION, null)
        }
        binding?.labelDaggerHilt?.setOnClickListener {
            currentActivity.openDetail(Globals.DAGGER_HILT, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}