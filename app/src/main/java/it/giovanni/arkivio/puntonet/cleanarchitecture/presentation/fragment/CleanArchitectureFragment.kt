package it.giovanni.arkivio.puntonet.cleanarchitecture.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.CleanArchitectureLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals

class CleanArchitectureFragment : DetailFragment() {

    private var layoutBinding: CleanArchitectureLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.clean_architecture_title
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

    override fun onActionSearch(searchString: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = CleanArchitectureLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.labelRxjava?.setOnClickListener {
            currentActivity.openDetail(Globals.CLEAN_ARCHITECTURE_RXJAVA, null)
        }
        binding?.labelPagingCoroutines?.setOnClickListener {
            currentActivity.openDetail(Globals.CLEAN_ARCHITECTURE_PAGING, null)
        }
        binding?.labelWorkManager?.setOnClickListener {
            currentActivity.openDetail(Globals.CLEAN_ARCHITECTURE_WORKER, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}