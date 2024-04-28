package it.giovanni.arkivio.fragments.detail.datemanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.DateLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.SharedPreferencesManager.resetSelectedDays

class DateFragment : DetailFragment() {

    private var layoutBinding: DateLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.date_title
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
        layoutBinding = DateLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.labelDateFormat?.setOnClickListener {
            currentActivity.openDetail(Globals.DATE_FORMAT, null)
        }
        binding?.labelDatePicker?.setOnClickListener {
            currentActivity.openDetail(Globals.DATE_PICKER, null)
        }
        binding?.labelCalendarviewHorizontal?.setOnClickListener {
            currentActivity.openDetail(Globals.CALENDARVIEW_HORIZONTAL, null)
        }
        binding?.labelCalendarviewVertical?.setOnClickListener {
            currentActivity.openDetail(Globals.CALENDARVIEW_VERTICAL, null)
        }
        binding?.labelSmartworking?.setOnClickListener {
            currentActivity.openDetail(Globals.SMARTWORKING, null)
        }
        binding?.resetSelectedDays?.setOnClickListener {
            resetSelectedDays()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}