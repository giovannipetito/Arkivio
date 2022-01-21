package it.giovanni.arkivio.fragments.detail.nearby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.NearbyLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals

class NearbyFragment: DetailFragment() {

    private var layoutBinding: NearbyLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getLayout(): Int {
        return NO_LAYOUT
    }

    override fun getTitle(): Int {
        return R.string.nearby_title
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
        layoutBinding = NearbyLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.labelNearbySearch?.setOnClickListener {
            currentActivity.openDetail(Globals.NEARBY_SEARCH, null)
        }
        binding?.labelNearbyChat?.setOnClickListener {
            currentActivity.openDetail(Globals.NEARBY_CHAT, null)
        }
        binding?.labelNearbyGame?.setOnClickListener {
            currentActivity.openDetail(Globals.NEARBY_GAME, null)
        }
        binding?.labelNearbyBeacons?.setOnClickListener {
            currentActivity.openDetail(Globals.NEARBY_BEACONS, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}