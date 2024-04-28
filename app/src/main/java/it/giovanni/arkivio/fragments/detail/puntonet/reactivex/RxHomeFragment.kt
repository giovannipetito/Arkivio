package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.RxHomeLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals

class RxHomeFragment : DetailFragment() {

    private var layoutBinding: RxHomeLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.rx_home_title
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
        layoutBinding = RxHomeLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.labelRxExample1?.setOnClickListener {
            currentActivity.openDetail(Globals.RX_EXAMPLE1, null)
        }
        binding?.labelRxExample2?.setOnClickListener {
            currentActivity.openDetail(Globals.RX_EXAMPLE2, null)
        }
        binding?.labelRxExample3?.setOnClickListener {
            currentActivity.openDetail(Globals.RX_EXAMPLE3, null)
        }
        binding?.labelRxExample4?.setOnClickListener {
            currentActivity.openDetail(Globals.RX_EXAMPLE4, null)
        }
        binding?.labelRxRetrofit?.setOnClickListener {
            currentActivity.openDetail(Globals.RX_RETROFIT, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}